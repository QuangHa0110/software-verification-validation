"""Generate one P3 (Direct LLM) JUnit 5 test suite in a single LLM call."""

import argparse
from collections.abc import Callable, Iterator, Sequence
from contextlib import contextmanager
from dataclasses import dataclass
from datetime import datetime, timezone
import hashlib
import inspect
import json
from pathlib import Path
import sys
from time import perf_counter

try:
    from tools.svv import llm_client
    from tools.svv.config import ROOT, load_config
    from tools.svv.llm_client import LlmError, LlmQuotaExceeded, LlmResult
    from tools.svv.test_blocks import extract_blocks
    from tools.svv.test_filter import FilterResult, filter_tests, write_suite
except ModuleNotFoundError:
    from svv import llm_client
    from svv.config import ROOT, load_config
    from svv.llm_client import LlmError, LlmQuotaExceeded, LlmResult
    from svv.test_blocks import extract_blocks
    from svv.test_filter import FilterResult, filter_tests, write_suite

METHOD = "p3-direct-llm"
SPEC_ONLY_METHOD = "p3r-spec-only"
VARIANTS = {"full": METHOD, "spec-only": SPEC_ONLY_METHOD}
PURPOSE = "direct"
CLASS_PREFIX = "P3"
PROMPT_VERSION = "1.0"

CANONICAL_SPEC_ORDER: tuple[str, ...] = (
    "README.md",
    "domain.md",
    "cart.md",
    "pricing.md",
    "shipping.md",
    "order.md",
    "branches.md",
)
EXCLUDED_SPEC_FILES: frozenset[str] = frozenset({"api.md", "open-questions.md"})
REQUIRED_PLACEHOLDERS: tuple[str, ...] = ("{{API}}", "{{SPEC}}", "{{SOURCE}}")


@dataclass(frozen=True)
class PromptBundle:
    text: str
    version: str
    system_sha256: str
    generate_sha256: str
    template_sha256: str
    rendered_sha256: str
    chars: int
    spec_files: tuple[str, ...]
    source_files: tuple[str, ...]


def _is_positive_int(value: object) -> bool:
    return isinstance(value, int) and not isinstance(value, bool) and value >= 1


def _is_positive_number(value: object) -> bool:
    return isinstance(value, (int, float)) and not isinstance(value, bool) and value > 0


def _read_lf(path: Path) -> str:
    return path.read_text(encoding="utf-8").replace("\r\n", "\n").replace("\r", "\n")


def _sha256_text(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def _ordered_spec_paths(spec_dir: Path) -> list[Path]:
    all_md = {path.name: path for path in spec_dir.glob("*.md") if path.is_file()}
    ordered: list[Path] = []
    seen: set[str] = set()
    for name in CANONICAL_SPEC_ORDER:
        if name in all_md and name not in EXCLUDED_SPEC_FILES:
            ordered.append(all_md[name])
            seen.add(name)
    for name in sorted(all_md):
        if name not in seen and name not in EXCLUDED_SPEC_FILES:
            ordered.append(all_md[name])
    return ordered


def _ordered_source_paths(source_dir: Path) -> list[Path]:
    return sorted(
        (path for path in source_dir.glob("*.java") if path.is_file()),
        key=lambda p: p.name,
    )


def build_prompt(*, root: Path = ROOT, variant: str = "full") -> PromptBundle:
    """Assemble the deterministic P3 direct prompt from templates, specs, and subject source."""
    if variant not in VARIANTS:
        raise ValueError(f"Unknown P3 variant: {variant}")
    full = variant == "full"
    prompt_dir = root / "prompts" / ("direct" if full else "direct-spec-only")
    system_path = prompt_dir / "system.md"
    generate_path = prompt_dir / "generate.md"
    spec_dir = root / "docs/spec"
    api_path = spec_dir / "api.md"
    source_dir = root / "subject/src/main/java/shop"

    system_text = _read_lf(system_path).strip()
    generate_text = _read_lf(generate_path).strip()

    required = REQUIRED_PLACEHOLDERS if full else ("{{API}}", "{{SPEC}}")
    if not full and any("{{SOURCE}}" in part for part in (system_text, generate_text)):
        raise ValueError(f"{prompt_dir}: spec-only templates cannot include {{{{SOURCE}}}}")
    missing = [token for token in required if token not in generate_text]
    if missing:
        raise ValueError(f"{generate_path}: missing required placeholder(s): {', '.join(missing)}")

    api_text = _read_lf(api_path).strip()
    spec_paths = _ordered_spec_paths(spec_dir)
    if not spec_paths:
        raise ValueError(f"{spec_dir}: no specification markdown files found")
    source_paths = _ordered_source_paths(source_dir) if full else []
    if full and not source_paths:
        raise ValueError(f"{source_dir}: no Java source files found")

    spec_sections = [
        f"### {path.relative_to(root).as_posix()}\n\n{_read_lf(path).strip()}"
        for path in spec_paths
    ]
    spec_text = "\n\n---\n\n".join(spec_sections)

    source_sections = [
        f"### {path.relative_to(root).as_posix()}\n\n```java\n{_read_lf(path).strip()}\n```"
        for path in source_paths
    ]
    source_text = "\n\n".join(source_sections)

    rendered_body = (
        generate_text
        .replace("{{API}}", api_text)
        .replace("{{SPEC}}", spec_text)
        .replace("{{SOURCE}}", source_text)
    )
    full_prompt = f"{system_text}\n\n---\n\n{rendered_body}\n"

    remaining = [token for token in REQUIRED_PLACEHOLDERS if token in full_prompt]
    if remaining:
        raise ValueError(f"Unreplaced placeholder(s) in rendered prompt: {', '.join(remaining)}")

    system_raw = _read_lf(system_path)
    generate_raw = _read_lf(generate_path)
    spec_rel = (api_path.relative_to(root).as_posix(), *(p.relative_to(root).as_posix() for p in spec_paths))
    source_rel = tuple(p.relative_to(root).as_posix() for p in source_paths)

    return PromptBundle(
        text=full_prompt,
        version=PROMPT_VERSION,
        system_sha256=_sha256_text(system_raw),
        generate_sha256=_sha256_text(generate_raw),
        template_sha256=_sha256_text(f"{system_raw}\n---\n{generate_raw}"),
        rendered_sha256=_sha256_text(full_prompt),
        chars=len(full_prompt),
        spec_files=spec_rel,
        source_files=source_rel,
    )


@contextmanager
def _llm_timeout_override(timeout_s: int | float, *, config_dir: Path | None = None) -> Iterator[None]:
    """Ensure llm_client.complete uses P3's configured timeout_s without mutating config files."""
    orig_load_config = llm_client.load_config
    try:
        accepts_dir = len(inspect.signature(orig_load_config).parameters) >= 1
    except (TypeError, ValueError):
        accepts_dir = False

    def _patched_load_config(*args, **kwargs):
        if not args and not kwargs and config_dir is not None and accepts_dir:
            cfg = orig_load_config(config_dir)
        else:
            cfg = orig_load_config(*args, **kwargs)
        patched = dict(cfg)
        llm_section = dict(patched.get("llm", {}))
        llm_section["timeout_s"] = timeout_s
        patched["llm"] = llm_section
        return patched

    llm_client.load_config = _patched_load_config
    try:
        yield
    finally:
        llm_client.load_config = orig_load_config


def _call_llm(
    complete_fn: Callable[..., LlmResult],
    prompt: str,
    *,
    method: str,
    run: int,
    timeout_s: int | float,
    provider: str | None,
    model: str | None,
    effort: str | None,
    config_dir: Path | None,
) -> LlmResult:
    kwargs: dict[str, object] = {
        "method": method,
        "run": run,
        "purpose": PURPOSE,
        "provider": provider,
        "model": model,
        "effort": effort,
    }
    try:
        sig = inspect.signature(complete_fn)
        has_timeout = "timeout_s" in sig.parameters
    except (TypeError, ValueError):
        has_timeout = False

    if has_timeout:
        kwargs["timeout_s"] = timeout_s
        return complete_fn(prompt, **kwargs)

    with _llm_timeout_override(timeout_s, config_dir=config_dir):
        return complete_fn(prompt, **kwargs)


def generate(
    run: int,
    *,
    variant: str = "full",
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
    root: Path = ROOT,
    config_dir: Path | None = None,
    out_dir: Path | None = None,
    complete_fn: Callable[..., LlmResult] | None = None,
    filter_fn: Callable[..., FilterResult] | None = None,
) -> dict:
    """Run P3 direct LLM test generation, filter results, and save suite + generation.json."""
    if not _is_positive_int(run):
        raise ValueError("run must be a positive integer")

    cfg_dir = config_dir if config_dir is not None else root / "experiments/config"
    config = load_config(cfg_dir)
    p3_cfg = config.get("p3") or {}
    timeout_s = p3_cfg.get("timeout_s", config["llm"]["timeout_s"])
    if not _is_positive_number(timeout_s):
        raise ValueError("p3.timeout_s must be a positive number")

    bundle = build_prompt(root=root, variant=variant)
    method = VARIANTS[variant]
    call_complete = complete_fn if complete_fn is not None else llm_client.complete
    call_filter = filter_fn if filter_fn is not None else filter_tests

    started = datetime.now(timezone.utc)
    start_clock = perf_counter()

    llm_res = _call_llm(
        call_complete,
        bundle.text,
        method=method,
        run=run,
        timeout_s=timeout_s,
        provider=provider,
        model=model,
        effort=effort,
        config_dir=config_dir,
    )

    blocks = extract_blocks(llm_res.text)
    filter_res = call_filter(
        blocks,
        class_prefix=CLASS_PREFIX,
        start_index=1,
        root=root,
    )

    generation_s = perf_counter() - start_clock
    ended = datetime.now(timezone.utc)

    target_dir = out_dir if out_dir is not None else root / "test-suites" / method / f"run-{run:02d}"
    target_dir.mkdir(parents=True, exist_ok=True)
    for stale in target_dir.glob("*.java"):
        stale.unlink()
    write_suite(filter_res.kept, method=method, run=run, root=root, output_dir=target_dir)

    dropped_records = [{"name": item.name, "reason": item.reason} for item in filter_res.dropped]
    filter_summary = {
        "generated": filter_res.generated,
        "compiled": filter_res.compiled,
        "passed_reference": filter_res.passed_reference,
        "valid": filter_res.valid,
        "compile_rate": filter_res.compile_rate,
        "oracle_correct_rate": filter_res.oracle_rate,
        "dropped": dropped_records,
    }
    tests_summary = {
        "generated": filter_res.generated,
        "compiled": filter_res.compiled,
        "passed_reference": filter_res.passed_reference,
        "valid": filter_res.valid,
        "compile_rate": filter_res.compile_rate,
        "oracle_rate": filter_res.oracle_rate,
        "dropped": dropped_records,
    }

    record = {
        "method": method,
        "run": run,
        "started_at": started.isoformat(),
        "ended_at": ended.isoformat(),
        "generation_s": generation_s,
        "llm": {
            "provider": llm_res.provider,
            "calls": 1,
            "input_tokens": llm_res.input_tokens,
            "cached_input_tokens": llm_res.cached_input_tokens,
            "output_tokens": llm_res.output_tokens,
            "reasoning_output_tokens": llm_res.reasoning_output_tokens,
            "tool_events": llm_res.tool_events,
            "duration_s": llm_res.duration_s,
            "cost_usd": llm_res.cost_usd,
            "model": llm_res.model,
            "effort": llm_res.effort,
        },
        "prompt": {
            "version": bundle.version,
            "system_sha256": bundle.system_sha256,
            "generate_sha256": bundle.generate_sha256,
            "template_sha256": bundle.template_sha256,
            "rendered_sha256": bundle.rendered_sha256,
            "chars": bundle.chars,
            "spec_files": list(bundle.spec_files),
            "source_files": list(bundle.source_files),
        },
        "filter": filter_summary,
        "tests": tests_summary,
        "iterations": [],
    }

    metadata_path = target_dir / "generation.json"
    metadata_path.write_text(
        json.dumps(record, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
        newline="\n",
    )
    return record


def _format_rate(rate: float | None) -> str:
    return "n/a" if rate is None else f"{rate * 100:.1f}%"


def main(argv: Sequence[str] | None = None) -> int:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    if hasattr(sys.stderr, "reconfigure"):
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run", type=int, required=True, help="Run number (1..N)")
    parser.add_argument("--variant", choices=VARIANTS, default="full")
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Build and print prompt with character count without calling the LLM",
    )
    parser.add_argument(
        "--provider",
        choices=("codex-cli", "agy-cli", "anthropic"),
        default=None,
        help="Override llm.provider from experiments/config/default.yaml",
    )
    parser.add_argument("--model", default=None, help="Override llm.model from config")
    parser.add_argument("--effort", default=None, help="Override llm.effort from config")
    args = parser.parse_args(argv)

    if not _is_positive_int(args.run):
        print("Error: --run must be a positive integer", file=sys.stderr)
        return 1

    if args.dry_run:
        try:
            bundle = build_prompt(root=ROOT, variant=args.variant)
        except (OSError, ValueError) as exc:
            print(f"Dry-run failed: {exc}", file=sys.stderr)
            return 1
        print(bundle.text, end="")
        print(
            f"---\n[dry-run] method={VARIANTS[args.variant]} run={args.run:02d} chars={bundle.chars} "
            f"specs={len(bundle.spec_files)} sources={len(bundle.source_files)} "
            f"system_sha256={bundle.system_sha256} generate_sha256={bundle.generate_sha256} "
            f"rendered_sha256={bundle.rendered_sha256}"
        )
        return 0

    try:
        record = generate(
            args.run,
            variant=args.variant,
            provider=args.provider,
            model=args.model,
            effort=args.effort,
        )
    except LlmQuotaExceeded as exc:
        print(f"Quota exceeded: {exc}", file=sys.stderr)
        return 2
    except (LlmError, OSError, ValueError, KeyError) as exc:
        print(f"P3 generation failed: {exc}", file=sys.stderr)
        return 1

    flt = record["filter"]
    llm = record["llm"]
    print(
        f"run-{args.run:02d}: generated={flt['generated']} compiled={flt['compiled']} "
        f"passed_reference={flt['passed_reference']} valid={flt['valid']} "
        f"compile_rate={_format_rate(flt['compile_rate'])} "
        f"oracle_correct_rate={_format_rate(flt['oracle_correct_rate'])} "
        f"tokens={llm['input_tokens']}+{llm['output_tokens']} "
        f"generation_s={record['generation_s']:.2f}"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
