"""One measured, logged model call per complete()."""

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import warnings
from collections.abc import Iterator, Sequence
from contextlib import contextmanager
from dataclasses import asdict, dataclass
from datetime import datetime, timezone
from pathlib import Path
from time import perf_counter
from uuid import uuid4

from .config import ROOT, load_config

WINDOWS_CMD_LIMIT = 32767
DEFAULT_SCRATCH_DIR = ".work/llm-scratch"
DEFAULT_SMOKE_PROMPT = (
    "Viết một phương thức JUnit 5 @Test kiểm tra 1+1==2. "
    "Chỉ trả về một khối mã ```java. Không dùng công cụ."
)


class LlmError(RuntimeError):
    pass


class LlmQuotaExceeded(LlmError):
    pass


@dataclass
class LlmResult:
    provider: str
    model: str
    effort: str
    text: str = ""
    input_tokens: int = 0
    cached_input_tokens: int = 0
    output_tokens: int = 0
    reasoning_output_tokens: int = 0
    tool_events: int = 0
    duration_s: float = 0.0
    cost_usd: float | None = None


_QUOTA = re.compile(
    r"\b(rate[ _-]?limit(?:ed|ing)?|usage[ _-]?limit|(?:insufficient[ _-]?)?quota)\b"
    r"|you['’]ve hit your usage limit"
    r"|rate_limit_exceeded|usage_limit_reached|resource_exhausted"
    r"|too many requests|capacity exhausted",
    re.I,
)


def _as_text(data: bytes | str | None) -> str:
    if data is None:
        return ""
    if isinstance(data, bytes):
        return data.decode("utf-8", "replace")
    return data


def _resolve_executable(path: str) -> str:
    return shutil.which(path) or path


@contextmanager
def _scratch_dir(config: dict, prefix: str) -> Iterator[Path]:
    """Create a fresh empty directory under llm.scratch_dir and remove it after the call."""
    raw_dir = config.get("scratch_dir") or DEFAULT_SCRATCH_DIR
    base = Path(raw_dir)
    if not base.is_absolute():
        base = ROOT / base
    base.mkdir(parents=True, exist_ok=True)
    call_dir = base / f"{prefix}{uuid4().hex}"
    call_dir.mkdir(parents=False, exist_ok=False)
    try:
        yield call_dir
    finally:
        try:
            shutil.rmtree(call_dir)
        except OSError as exc:
            warnings.warn(
                f"Failed to remove LLM scratch directory {call_dir}: {exc}",
                RuntimeWarning,
                stacklevel=2,
            )


def _check_windows_cmd_limit(command: Sequence[str], limit: int = WINDOWS_CMD_LIMIT) -> int:
    cmdline_len = len(subprocess.list2cmdline(list(command)))
    if cmdline_len > limit:
        raise LlmError(
            f"Command line length ({cmdline_len} chars) exceeds Windows limit ({limit} chars)"
        )
    return cmdline_len


def _parse_codex(stdout: str, stderr: str, returncode: int | None, result: LlmResult) -> None:
    messages: list[str] = []
    tool_ids: set[str | None] = set()
    diagnostics: list[str] = []
    completed = False
    malformed = False
    for line in stdout.splitlines():
        if not line.strip():
            continue
        try:
            event = json.loads(line)
        except ValueError:
            diagnostics.append(line)
            malformed = True
            continue
        if not isinstance(event, dict) or "type" not in event:
            diagnostics.append(line)
            malformed = True
            continue
        kind = event["type"]
        item = event.get("item")
        if isinstance(item, dict):
            item_type = item.get("type")
            if item_type == "agent_message" and kind == "item.completed":
                messages.append(str(item.get("text", "")))
            elif item_type and item_type not in {"agent_message", "reasoning"}:
                # started/updated/completed are the lifecycle of one tool item.
                identity = item.get("id")
                if identity is None or identity not in tool_ids:
                    result.tool_events += 1
                    tool_ids.add(identity)
        if kind == "turn.completed":
            completed = True
            usage = event.get("usage") or {}
            for key in ("input_tokens", "cached_input_tokens", "output_tokens", "reasoning_output_tokens"):
                setattr(result, key, getattr(result, key) + int(usage.get(key) or 0))
        if kind in {"error", "turn.failed"}:
            diagnostics.append(json.dumps(event.get("error", event.get("message", event)), ensure_ascii=False))
    result.text = "\n".join(messages)
    detail = "\n".join([*diagnostics, stderr]).strip()
    if _QUOTA.search(detail) or ((returncode or not completed) and _QUOTA.search(stdout)):
        quota_detail = detail or stdout.strip()
        raise LlmQuotaExceeded(f"Codex quota or usage limit: {quota_detail}")
    if returncode is None:
        raise LlmError("Codex timed out before finishing the call")
    if returncode or not completed or malformed:
        reason = f"exit {returncode}"
        if not completed:
            reason += ", missing turn.completed"
        if malformed:
            reason += ", invalid JSONL"
        raise LlmError(f"Codex {reason}: {detail}")


def _codex(prompt: str, config: dict, result: LlmResult) -> None:
    executable = _resolve_executable(config.get("codex_path", "codex"))
    command = [
        executable, "exec", "--skip-git-repo-check", "--ephemeral", "--json",
        "-s", "read-only", "-c", f"model_reasoning_effort={result.effort}", "-m", result.model, "-",
    ]
    with _scratch_dir(config, "codex-") as cwd:
        try:
            proc = subprocess.run(
                command, input=prompt, cwd=cwd, capture_output=True, text=True,
                encoding="utf-8", errors="replace", shell=False, timeout=config["timeout_s"],
            )
        except subprocess.TimeoutExpired as exc:
            _parse_codex(_as_text(exc.stdout), _as_text(exc.stderr), None, result)
        else:
            _parse_codex(proc.stdout, proc.stderr, proc.returncode, result)


def _apply_agy_payload(payload: dict, stderr: str, returncode: int | None, result: LlmResult) -> None:
    usage = payload.get("usage") or {}
    result.text = str(payload.get("response") or "")
    result.input_tokens = int(usage.get("input_tokens") or 0)
    result.output_tokens = int(usage.get("output_tokens") or 0)
    result.reasoning_output_tokens = int(
        usage.get("thinking_tokens") or usage.get("reasoning_output_tokens") or 0
    )
    result.cached_input_tokens = int(
        usage.get("cache_read_tokens") or usage.get("cached_input_tokens") or 0
    )
    duration = payload.get("duration_seconds")
    if isinstance(duration, (int, float)) and duration > 0:
        result.duration_s = float(duration)

    status = str(payload.get("status") or "").strip().upper()
    err_raw = payload.get("error")
    if isinstance(err_raw, (dict, list)):
        err_msg = json.dumps(err_raw, ensure_ascii=False)
    else:
        err_msg = str(err_raw or "").strip()
    detail = "\n".join(part for part in (err_msg, stderr.strip()) if part).strip()
    failed = returncode is None or returncode != 0 or status not in {"SUCCESS", "OK", "COMPLETED"}
    if _QUOTA.search(detail) or (failed and _QUOTA.search(result.text)):
        raise LlmQuotaExceeded(f"agy quota or usage limit: {detail or result.text}")
    if returncode is None:
        raise LlmError("agy timed out before finishing the call")
    if failed:
        reason = f"exit {returncode}, status={status or 'missing'}"
        raise LlmError(f"agy {reason}: {detail or json.dumps(payload, ensure_ascii=False)}")


def _extract_agy_json(stdout: str) -> dict | None:
    text = stdout.strip()
    if not text:
        return None
    expected_keys = ("response", "status", "usage", "error")

    # 1. Whole stdout is a single JSON object (compact or pretty-printed).
    try:
        parsed = json.loads(text)
    except ValueError:
        parsed = None
    if isinstance(parsed, dict) and any(k in parsed for k in expected_keys):
        return parsed

    # 2. Single-line JSON object preceded by diagnostic lines.
    for line in reversed(text.splitlines()):
        stripped = line.strip()
        if not stripped:
            continue
        try:
            parsed = json.loads(stripped)
        except ValueError:
            continue
        if isinstance(parsed, dict) and any(k in parsed for k in expected_keys):
            return parsed

    # 3. Pretty-printed JSON object preceded by diagnostic lines.
    decoder = json.JSONDecoder()
    idx = text.rfind("{")
    while idx != -1:
        try:
            parsed, _ = decoder.raw_decode(text, idx)
        except ValueError:
            idx = text.rfind("{", 0, idx)
            continue
        if isinstance(parsed, dict) and any(k in parsed for k in expected_keys):
            return parsed
        idx = text.rfind("{", 0, idx)
    return None


def _parse_agy(stdout: str, stderr: str, returncode: int | None, result: LlmResult) -> None:
    text = stdout.strip()
    detail = "\n".join(part for part in (text, stderr.strip()) if part).strip()
    if not text:
        if _QUOTA.search(detail):
            raise LlmQuotaExceeded(f"agy quota or usage limit: {detail}")
        if returncode is None:
            raise LlmError("agy timed out before finishing the call")
        raise LlmError(f"agy exit {returncode}, empty JSON output: {detail}")

    payload = _extract_agy_json(stdout)
    if payload is None:
        if _QUOTA.search(detail):
            raise LlmQuotaExceeded(f"agy quota or usage limit: {detail}")
        if returncode is None:
            raise LlmError("agy timed out before finishing the call")
        raise LlmError(f"agy exit {returncode}, invalid JSON output: {detail}")
    _apply_agy_payload(payload, stderr, returncode, result)


def _parse_agy_stream(stdout: str, stderr: str, returncode: int | None, result: LlmResult) -> None:
    tool_steps: set[int] = set()
    diagnostics: list[str] = []
    payload: dict | None = None
    non_tool_steps = {"user_input", "agent_response", "thinking", "thought", "reasoning"}

    for line in stdout.splitlines():
        if not line.strip():
            continue
        try:
            event = json.loads(line)
        except ValueError:
            diagnostics.append(line)
            continue
        if not isinstance(event, dict):
            diagnostics.append(line)
            continue
        kind = event.get("event")
        if kind == "step_update":
            step = event.get("step_update") or {}
            step_type = step.get("step_type")
            if step_type and step_type not in non_tool_steps:
                idx = step.get("step_index")
                if idx is None or idx not in tool_steps:
                    result.tool_events += 1
                    if idx is not None:
                        tool_steps.add(idx)
        elif kind == "result" and isinstance(event.get("result"), dict):
            payload = event["result"]

    extra_err = "\n".join([*diagnostics, stderr]).strip()
    if payload is None:
        if _QUOTA.search(extra_err):
            raise LlmQuotaExceeded(f"agy quota or usage limit: {extra_err}")
        if returncode is None:
            raise LlmError("agy timed out before finishing the call")
        raise LlmError(f"agy exit {returncode}, missing result event: {extra_err}")
    _apply_agy_payload(payload, extra_err, returncode, result)


def _agy_cli_command(executable: str, prompt: str, model: str, timeout_s: int) -> list[str]:
    if not prompt or prompt.startswith("-"):
        prompt_args = [f"-p={prompt}"]
    else:
        prompt_args = ["-p", prompt]
    model_args = ["--model", model] if model else []
    return [
        executable, *prompt_args, *model_args,
        "--output-format", "json", "--print-timeout", f"{int(timeout_s)}s",
    ]


def _agy(prompt: str, config: dict, result: LlmResult) -> None:
    executable = _resolve_executable(config.get("agy_path", "agy"))
    timeout_s = int(config["timeout_s"])
    command = _agy_cli_command(executable, prompt, result.model, timeout_s)
    cmdline_len = len(subprocess.list2cmdline(command))
    use_stream = cmdline_len > WINDOWS_CMD_LIMIT
    if use_stream and not config.get("agy_stream_long_prompts", True):
        _check_windows_cmd_limit(command)

    with _scratch_dir(config, "agy-") as cwd:
        if use_stream:
            model_args = ["--model", result.model] if result.model else []
            stream_cmd = [
                executable, *model_args,
                "--input-format", "stream-json", "--output-format", "stream-json",
                "--print-timeout", f"{timeout_s}s",
            ]
            stdin_payload = json.dumps(
                {"event": "user", "message": {"content": prompt}},
                ensure_ascii=False,
            ) + "\n"
            try:
                proc = subprocess.run(
                    stream_cmd, input=stdin_payload, cwd=cwd, capture_output=True, text=True,
                    encoding="utf-8", errors="replace", shell=False, timeout=timeout_s + 15,
                )
            except subprocess.TimeoutExpired as exc:
                _parse_agy_stream(_as_text(exc.stdout), _as_text(exc.stderr), None, result)
            else:
                _parse_agy_stream(proc.stdout, proc.stderr, proc.returncode, result)
        else:
            try:
                proc = subprocess.run(
                    command, cwd=cwd, capture_output=True, text=True,
                    encoding="utf-8", errors="replace", shell=False, timeout=timeout_s + 15,
                )
            except subprocess.TimeoutExpired as exc:
                _parse_agy(_as_text(exc.stdout), _as_text(exc.stderr), None, result)
            else:
                _parse_agy(proc.stdout, proc.stderr, proc.returncode, result)


def _anthropic_key() -> str:
    key = os.environ.get("ANTHROPIC_API_KEY")
    if key:
        return key
    path = ROOT / ".env"
    if path.is_file():
        for line in path.read_text(encoding="utf-8").splitlines():
            match = re.fullmatch(
                r"""\s*(?:export\s+)?ANTHROPIC_API_KEY\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s#]+))\s*(?:#.*)?""",
                line,
            )
            if match:
                value = next(v for v in match.groups() if v is not None)
                if value:
                    return value
    raise LlmError("Missing ANTHROPIC_API_KEY in the environment or repository .env")


def _anthropic(prompt: str, config: dict, result: LlmResult, key: str) -> None:
    import anthropic

    try:
        with anthropic.Anthropic(api_key=key, timeout=config["timeout_s"], max_retries=0) as client:
            message = client.messages.create(
                model=result.model,
                max_tokens=config["max_output_tokens"],
                output_config={"effort": result.effort},
                messages=[{"role": "user", "content": prompt}],
            )
    except anthropic.APIError as exc:
        if isinstance(exc, anthropic.RateLimitError) or _QUOTA.search(str(exc)):
            raise LlmQuotaExceeded("Anthropic quota or usage limit reached") from None
        raise LlmError(f"Anthropic request failed ({type(exc).__name__})") from None
    result.text = "\n".join(block.text for block in message.content if block.type == "text")
    result.tool_events = sum(block.type in {"tool_use", "server_tool_use"} for block in message.content)
    usage = message.usage
    result.cached_input_tokens = getattr(usage, "cache_read_input_tokens", 0) or 0
    result.input_tokens = (
        (getattr(usage, "input_tokens", 0) or 0)
        + result.cached_input_tokens
        + (getattr(usage, "cache_creation_input_tokens", 0) or 0)
    )
    result.output_tokens = getattr(usage, "output_tokens", 0) or 0
    details = getattr(usage, "output_tokens_details", None)
    result.reasoning_output_tokens = getattr(details, "thinking_tokens", 0) or 0


def complete(
    prompt: str,
    *,
    method: str,
    run: int,
    purpose: str,
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
) -> LlmResult:
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9_-]*", method) or run < 1:
        raise ValueError("Expected a method identifier and a positive run number")
    config = load_config()["llm"]
    result = LlmResult(
        provider or config["provider"],
        model or config["model"],
        effort or config["effort"],
    )
    directory = ROOT / "results/llm-logs" / method / f"run-{run:02d}"
    directory.mkdir(parents=True, exist_ok=True)
    started = datetime.now(timezone.utc)
    start = perf_counter()
    error = None
    key = os.environ.get("ANTHROPIC_API_KEY")
    try:
        if result.provider == "codex-cli":
            _codex(prompt, config, result)
        elif result.provider == "agy-cli":
            _agy(prompt, config, result)
        elif result.provider == "anthropic":
            key = _anthropic_key()
            _anthropic(prompt, config, result, key)
        else:
            raise LlmError(f"Unknown LLM provider: {result.provider}")
    except Exception as exc:
        error = str(exc)
        if key:
            error = error.replace(key, "[REDACTED]")
        if isinstance(exc, LlmQuotaExceeded):
            raise LlmQuotaExceeded(error) from None
        raise LlmError(error) from None
    finally:
        if result.duration_s <= 0.0:
            result.duration_s = perf_counter() - start
        p_in, p_out = config["price_per_mtok_input"], config["price_per_mtok_output"]
        if p_in is not None and p_out is not None:
            result.cost_usd = (result.input_tokens * p_in + result.output_tokens * p_out) / 1_000_000
        record = asdict(result)
        record.update(
            started_at=started.isoformat(),
            ended_at=datetime.now(timezone.utc).isoformat(),
            purpose=purpose,
            prompt=prompt,
            response=record.pop("text"),
            error=error,
        )
        if key:
            record = {k: v.replace(key, "[REDACTED]") if isinstance(v, str) else v for k, v in record.items()}
        with (directory / "calls.jsonl").open("a", encoding="utf-8") as log:
            log.write(json.dumps(record, ensure_ascii=False) + "\n")
    return result


def main(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="llm_client",
        description="Run a single measured, logged LLM call (e.g., Spike S3 smoke test).",
    )
    parser.add_argument("--smoke", action="store_true", help="Run the default Spike S3 smoke prompt")
    parser.add_argument("--prompt", default=None, help="Explicit prompt string (overrides --smoke prompt)")
    parser.add_argument(
        "--provider",
        choices=("codex-cli", "agy-cli", "anthropic"),
        default=None,
        help="Override llm.provider from experiments/config/default.yaml",
    )
    parser.add_argument("--model", default=None, help="Override llm.model from config")
    parser.add_argument("--effort", default=None, help="Override llm.effort from config")
    parser.add_argument("--method", default="spike-s3", help="Method folder under results/llm-logs/ (default: spike-s3)")
    parser.add_argument("--run", type=int, default=1, help="Run number (default: 1)")
    parser.add_argument("--purpose", default="smoke", help="Purpose tag written to calls.jsonl (default: smoke)")
    args = parser.parse_args(argv)

    if args.prompt is not None:
        prompt = args.prompt
    elif args.smoke:
        prompt = DEFAULT_SMOKE_PROMPT
    else:
        parser.error("Provide --smoke or --prompt")

    try:
        res = complete(
            prompt,
            method=args.method,
            run=args.run,
            purpose=args.purpose,
            provider=args.provider,
            model=args.model,
            effort=args.effort,
        )
    except LlmQuotaExceeded as exc:
        print(f"Quota exceeded: {exc}", file=sys.stderr)
        return 2
    except (LlmError, ValueError) as exc:
        print(f"LLM call failed: {exc}", file=sys.stderr)
        return 1

    print(json.dumps(asdict(res), ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main())
