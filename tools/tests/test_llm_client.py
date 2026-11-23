import json
import subprocess
from pathlib import Path
from types import SimpleNamespace

import anthropic
import pytest

from tools.svv import llm_client
from tools.svv.llm_client import (
    WINDOWS_CMD_LIMIT,
    LlmError,
    LlmQuotaExceeded,
    LlmResult,
    _check_windows_cmd_limit,
    _parse_agy,
    _parse_agy_stream,
    _parse_codex,
    complete,
)

FIXTURES = Path(__file__).parent / "fixtures"


def _base_config(**overrides) -> dict:
    llm = {
        "provider": "codex-cli",
        "codex_path": "codex",
        "agy_path": "agy",
        "scratch_dir": ".work/llm-scratch",
        "timeout_s": 60,
        "model": "gpt-6.2-astra",
        "effort": "medium",
        "max_output_tokens": 4096,
        "price_per_mtok_input": None,
        "price_per_mtok_output": None,
    }
    llm.update(overrides)
    return {"llm": llm}


def test_parse_codex_fixture_success_and_tool_dedup():
    raw = (FIXTURES / "codex_ok.jsonl").read_text(encoding="utf-8")
    result = LlmResult("codex-cli", "gpt-6.2-astra", "medium")
    _parse_codex(raw, "", 0, result)
    assert "void onePlusOne()" in result.text
    assert result.text.endswith("\nDone.")
    assert result.input_tokens == 22150
    assert result.cached_input_tokens == 18000
    assert result.output_tokens == 42
    assert result.reasoning_output_tokens == 15
    # cmd-1 started+completed counts once, file-1 counts once; reasoning and agent_message ignored
    assert result.tool_events == 2


def test_parse_codex_fixture_quota_exceeded():
    raw = (FIXTURES / "codex_quota.jsonl").read_text(encoding="utf-8")
    result = LlmResult("codex-cli", "gpt-6.2-astra", "medium")
    with pytest.raises(LlmQuotaExceeded, match="You've hit your usage limit"):
        _parse_codex(raw, "", 1, result)


def test_parse_codex_fixture_error_and_malformed_and_timeout():
    raw = (FIXTURES / "codex_error.jsonl").read_text(encoding="utf-8")
    result = LlmResult("codex-cli", "gpt-6.2-astra", "medium")
    with pytest.raises(LlmError, match="missing turn.completed"):
        _parse_codex(raw, "", 1, result)

    with pytest.raises(LlmError, match="invalid JSONL"):
        _parse_codex('not-json\n{"type":"turn.completed","usage":{}}\n', "", 0, LlmResult("codex-cli", "m", "low"))

    with pytest.raises(LlmError, match="timed out"):
        _parse_codex("", "", None, LlmResult("codex-cli", "m", "low"))


def test_complete_codex_runs_in_empty_scratch_dir_and_logs_cost(tmp_path, monkeypatch):
    raw = (FIXTURES / "codex_ok.jsonl").read_text(encoding="utf-8")
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(
        llm_client,
        "load_config",
        lambda: _base_config(price_per_mtok_input=2.0, price_per_mtok_output=10.0),
    )

    captured = {}

    def fake_run(cmd, **kwargs):
        captured["cmd"] = cmd
        captured["kwargs"] = kwargs
        cwd = Path(kwargs["cwd"]).resolve()
        captured["cwd"] = cwd
        assert cwd.is_dir()
        assert not list(cwd.iterdir())
        assert cwd.parent == (tmp_path / ".work/llm-scratch").resolve()
        return subprocess.CompletedProcess(cmd, 0, stdout=raw, stderr="")

    monkeypatch.setattr(llm_client.subprocess, "run", fake_run)
    res = complete("Write test", method="p3-direct-llm", run=1, purpose="unit")

    assert not captured["cwd"].exists()
    assert captured["kwargs"]["shell"] is False
    assert captured["kwargs"]["input"] == "Write test"
    assert "-s" in captured["cmd"] and "read-only" in captured["cmd"]
    assert res.input_tokens == 22150
    assert res.output_tokens == 42
    assert res.cost_usd == pytest.approx((22150 * 2.0 + 42 * 10.0) / 1_000_000)

    log_file = tmp_path / "results/llm-logs/p3-direct-llm/run-01/calls.jsonl"
    lines = log_file.read_text(encoding="utf-8").splitlines()
    assert len(lines) == 1
    rec = json.loads(lines[0])
    assert rec["provider"] == "codex-cli"
    assert rec["purpose"] == "unit"
    assert rec["prompt"] == "Write test"
    assert rec["error"] is None
    assert rec["tool_events"] == 2
    assert rec["started_at"] and rec["ended_at"]


def test_scratch_dir_custom_path_and_cleanup_failure_only_warns(tmp_path, monkeypatch):
    raw = (FIXTURES / "codex_ok.jsonl").read_text(encoding="utf-8")
    custom_scratch = tmp_path / "custom-scratch"
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(
        llm_client,
        "load_config",
        lambda: _base_config(scratch_dir=str(custom_scratch)),
    )

    seen_cwd = []

    def fake_run(cmd, **kwargs):
        cwd = Path(kwargs["cwd"]).resolve()
        seen_cwd.append(cwd)
        assert cwd.parent == custom_scratch.resolve()
        assert not list(cwd.iterdir())
        return subprocess.CompletedProcess(cmd, 0, stdout=raw, stderr="")

    def failing_rmtree(path):
        raise PermissionError(f"locked: {path}")

    monkeypatch.setattr(llm_client.subprocess, "run", fake_run)
    monkeypatch.setattr(llm_client.shutil, "rmtree", failing_rmtree)

    with pytest.warns(RuntimeWarning, match="Failed to remove LLM scratch directory"):
        res = complete("Write test", method="spike-s3", run=1, purpose="warn-cleanup")

    assert res.output_tokens == 42
    assert seen_cwd[0].exists()


def test_complete_codex_logs_error_on_quota(tmp_path, monkeypatch):
    raw = (FIXTURES / "codex_quota.jsonl").read_text(encoding="utf-8")
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(llm_client, "load_config", lambda: _base_config())
    monkeypatch.setattr(
        llm_client.subprocess,
        "run",
        lambda cmd, **kwargs: subprocess.CompletedProcess(cmd, 1, stdout=raw, stderr=""),
    )

    with pytest.raises(LlmQuotaExceeded, match="You've hit your usage limit"):
        complete("Hi", method="spike-s3", run=1, purpose="quota-test")

    log_file = tmp_path / "results/llm-logs/spike-s3/run-01/calls.jsonl"
    rec = json.loads(log_file.read_text(encoding="utf-8").strip())
    assert rec["cost_usd"] is None
    assert "You've hit your usage limit" in rec["error"]


def test_parse_agy_single_line_and_quota_and_stream():
    ok_raw = (FIXTURES / "agy_ok.json").read_text(encoding="utf-8")
    res = LlmResult("agy-cli", "gemini-3.8-flash-low", "low")
    _parse_agy(ok_raw, "", 0, res)
    assert "void onePlusOne()" in res.text
    assert res.duration_s == pytest.approx(4.25)
    assert res.input_tokens == 15525
    assert res.output_tokens == 38
    assert res.reasoning_output_tokens == 120
    assert res.cached_input_tokens == 12000

    quota_raw = (FIXTURES / "agy_quota.json").read_text(encoding="utf-8")
    with pytest.raises(LlmQuotaExceeded, match="quota"):
        _parse_agy(quota_raw, "", 1, LlmResult("agy-cli", "gemini-3.8-flash-low", "low"))

    stream_raw = (FIXTURES / "agy_stream_ok.jsonl").read_text(encoding="utf-8")
    sres = LlmResult("agy-cli", "gemini-3.8-flash-low", "low")
    _parse_agy_stream(stream_raw, "", 0, sres)
    assert sres.text == "Streamed response"
    assert sres.duration_s == pytest.approx(6.5)
    assert sres.input_tokens == 20000
    assert sres.output_tokens == 50
    assert sres.reasoning_output_tokens == 80
    assert sres.cached_input_tokens == 14000
    assert sres.tool_events == 1


def test_complete_agy_short_and_long_prompt_and_windows_limit(tmp_path, monkeypatch):
    ok_raw = (FIXTURES / "agy_ok.json").read_text(encoding="utf-8")
    stream_raw = (FIXTURES / "agy_stream_ok.jsonl").read_text(encoding="utf-8")
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(
        llm_client,
        "load_config",
        lambda: _base_config(provider="agy-cli", model="gemini-3.8-flash-low", effort="low", timeout_s=90),
    )

    calls = []

    def fake_run(cmd, **kwargs):
        calls.append((cmd, kwargs))
        cwd = Path(kwargs["cwd"]).resolve()
        assert cwd.is_dir() and not list(cwd.iterdir())
        if "--input-format" in cmd:
            return subprocess.CompletedProcess(cmd, 0, stdout=stream_raw, stderr="")
        return subprocess.CompletedProcess(cmd, 0, stdout=ok_raw, stderr="")

    monkeypatch.setattr(llm_client.subprocess, "run", fake_run)

    # Short prompt uses -p <prompt> --model <model> --output-format json --print-timeout <timeout>s
    res_short = complete("Write 1+1 test", method="spike-s3", run=1, purpose="agy-short")
    short_cmd, _ = calls[0]
    assert "-p" in short_cmd and "Write 1+1 test" in short_cmd
    assert "--model" in short_cmd and "gemini-3.8-flash-low" in short_cmd
    assert "--output-format" in short_cmd and "json" in short_cmd
    assert "--print-timeout" in short_cmd and "90s" in short_cmd
    assert res_short.reasoning_output_tokens == 120
    assert res_short.cached_input_tokens == 12000

    # Long prompt (> 32767 chars) switches to stream-json via stdin
    long_prompt = "X" * (WINDOWS_CMD_LIMIT + 500)
    res_long = complete(long_prompt, method="spike-s3", run=1, purpose="agy-long")
    long_cmd, long_kwargs = calls[1]
    assert "--input-format" in long_cmd and "stream-json" in long_cmd
    sent = json.loads(long_kwargs["input"].strip())
    assert sent == {"event": "user", "message": {"content": long_prompt}}
    assert res_long.text == "Streamed response"

    # Explicit Windows cmdline length check raises clear error mentioning 32767
    with pytest.raises(LlmError, match=str(WINDOWS_CMD_LIMIT)):
        _check_windows_cmd_limit(["agy", "-p", long_prompt])

    monkeypatch.setattr(
        llm_client,
        "load_config",
        lambda: _base_config(provider="agy-cli", model="gemini-3.8-flash-low", agy_stream_long_prompts=False),
    )
    with pytest.raises(LlmError, match=str(WINDOWS_CMD_LIMIT)):
        complete(long_prompt, method="spike-s3", run=1, purpose="agy-limit-err")


def test_complete_anthropic_mock_reads_dotenv_and_redacts_key(tmp_path, monkeypatch):
    secret = "FAKE-TEST-KEY-not-a-real-secret-12345"
    (tmp_path / ".env").write_text(f'ANTHROPIC_API_KEY="{secret}"\n', encoding="utf-8")
    monkeypatch.delenv("ANTHROPIC_API_KEY", raising=False)
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(
        llm_client,
        "load_config",
        lambda: _base_config(
            provider="anthropic",
            model="claude-sonnet-5-5",
            effort="medium",
            price_per_mtok_input=3.0,
            price_per_mtok_output=15.0,
        ),
    )

    captured = {}

    class FakeClient:
        def __init__(self, api_key, timeout, max_retries):
            captured["api_key"] = api_key
            captured["timeout"] = timeout
            captured["max_retries"] = max_retries
            self.messages = SimpleNamespace(create=self._create)

        def __enter__(self):
            return self

        def __exit__(self, *args):
            return False

        def _create(self, **kwargs):
            captured["create_kwargs"] = kwargs
            return SimpleNamespace(
                content=[
                    SimpleNamespace(type="text", text="hello"),
                    SimpleNamespace(type="server_tool_use"),
                ],
                usage=SimpleNamespace(
                    input_tokens=100,
                    cache_read_input_tokens=20,
                    cache_creation_input_tokens=5,
                    output_tokens=50,
                    output_tokens_details=SimpleNamespace(thinking_tokens=12),
                ),
            )

    monkeypatch.setattr(anthropic, "Anthropic", FakeClient)
    res = complete("Prompt text", method="p3-direct-llm", run=2, purpose="anthropic-mock")

    assert captured["api_key"] == secret
    assert captured["create_kwargs"]["output_config"] == {"effort": "medium"}
    assert res.text == "hello"
    assert res.input_tokens == 125
    assert res.cached_input_tokens == 20
    assert res.output_tokens == 50
    assert res.reasoning_output_tokens == 12
    assert res.tool_events == 1
    assert res.cost_usd == pytest.approx((125 * 3.0 + 50 * 15.0) / 1_000_000)

    log_text = (tmp_path / "results/llm-logs/p3-direct-llm/run-02/calls.jsonl").read_text(encoding="utf-8")
    assert secret not in log_text


def test_complete_anthropic_rate_limit_raises_quota_and_redacts(tmp_path, monkeypatch):
    secret = "FAKE-TEST-KEY-not-a-real-secret-999"
    monkeypatch.setenv("ANTHROPIC_API_KEY", secret)
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(llm_client, "load_config", lambda: _base_config(provider="anthropic"))

    class FakeRateLimitClient:
        def __init__(self, **kwargs):
            self.messages = SimpleNamespace(create=self._create)

        def __enter__(self):
            return self

        def __exit__(self, *args):
            return False

        def _create(self, **kwargs):
            req = SimpleNamespace()
            resp = SimpleNamespace(status_code=429, headers={}, request=req)
            raise anthropic.RateLimitError(f"Rate limit exceeded for {secret}", response=resp, body=None)

    monkeypatch.setattr(anthropic, "Anthropic", FakeRateLimitClient)
    with pytest.raises(LlmQuotaExceeded, match="quota or usage limit"):
        complete("Ping", method="p3-direct-llm", run=3, purpose="rate-limit")

    log_text = (tmp_path / "results/llm-logs/p3-direct-llm/run-03/calls.jsonl").read_text(encoding="utf-8")
    assert secret not in log_text
    assert json.loads(log_text.strip())["error"] is not None


def test_parse_agy_pretty_printed_json_and_usage_limit_phrase():
    pretty = json.dumps(
        {
            "status": "SUCCESS",
            "response": "```java\n@Test void ok() {}\n```",
            "duration_seconds": 1.5,
            "usage": {"input_tokens": 100, "output_tokens": 20, "thinking_tokens": 5, "cache_read_tokens": 10},
        },
        indent=2,
    )
    res = LlmResult("agy-cli", "gemini-3.8-flash-low", "low")
    _parse_agy("Starting session...\n" + pretty + "\n", "", 0, res)
    assert res.input_tokens == 100
    assert res.output_tokens == 20
    assert res.reasoning_output_tokens == 5
    assert res.cached_input_tokens == 10

    with pytest.raises(LlmQuotaExceeded, match="You've hit your usage limit"):
        _parse_agy(
            json.dumps({"status": "ERROR", "error": "You've hit your usage limit. Try later."}),
            "",
            1,
            LlmResult("agy-cli", "gemini-3.8-flash-low", "low"),
        )


def test_cli_smoke_command_uses_provider_override_and_outputs_json(tmp_path, monkeypatch, capsys):
    ok_raw = (FIXTURES / "agy_ok.json").read_text(encoding="utf-8")
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(llm_client, "load_config", lambda: _base_config(provider="codex-cli"))

    seen = {}

    def fake_run(cmd, **kwargs):
        seen["cmd"] = cmd
        seen["cwd"] = Path(kwargs["cwd"]).resolve()
        assert seen["cwd"].parent == (tmp_path / ".work/llm-scratch").resolve()
        return subprocess.CompletedProcess(cmd, 0, stdout=ok_raw, stderr="")

    monkeypatch.setattr(llm_client.subprocess, "run", fake_run)
    rc = llm_client.main(["--smoke", "--provider", "agy-cli"])
    assert rc == 0
    assert not seen["cwd"].exists()
    out = json.loads(capsys.readouterr().out)
    assert out["provider"] == "agy-cli"
    assert out["input_tokens"] == 15525
    assert out["output_tokens"] == 38
    assert (tmp_path / "results/llm-logs/spike-s3/run-01/calls.jsonl").is_file()


def test_anthropic_empty_key_in_dotenv_raises_error(tmp_path, monkeypatch):
    (tmp_path / ".env").write_text('ANTHROPIC_API_KEY=""\n', encoding="utf-8")
    monkeypatch.delenv("ANTHROPIC_API_KEY", raising=False)
    monkeypatch.setattr(llm_client, "ROOT", tmp_path)
    monkeypatch.setattr(llm_client, "load_config", lambda: _base_config(provider="anthropic"))
    with pytest.raises(LlmError, match="Missing ANTHROPIC_API_KEY"):
        complete("Hi", method="spike-s3", run=1, purpose="empty-key")
