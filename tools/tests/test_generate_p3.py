import hashlib
import json
from pathlib import Path
import shutil
import subprocess
from uuid import uuid4

import pytest

from experiments.scripts import measure
from tools import generate_p3
from tools.svv import llm_client
from tools.svv.llm_client import LlmError, LlmQuotaExceeded, LlmResult
from tools.svv.test_blocks import TestBlock
from tools.svv.test_filter import (
    REASON_COMPILE_ERROR,
    REASON_DUPLICATE,
    REASON_FAILS_ON_REFERENCE,
    DroppedTest,
    FilterResult,
    FilteredBlock,
)

FIXTURES = Path(__file__).parent / "fixtures"


@pytest.fixture
def workspace():
    path = generate_p3.ROOT / ".work" / f"test-p3-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def test_build_prompt_deterministic_order_replaces_placeholders_and_hashes():
    bundle_a = generate_p3.build_prompt()
    bundle_b = generate_p3.build_prompt()

    assert bundle_a == bundle_b
    assert bundle_a.chars == len(bundle_a.text)
    assert bundle_a.chars > 10_000
    for token in generate_p3.REQUIRED_PLACEHOLDERS:
        assert token not in bundle_a.text

    assert bundle_a.spec_files == (
        "docs/spec/api.md",
        "docs/spec/README.md",
        "docs/spec/domain.md",
        "docs/spec/cart.md",
        "docs/spec/pricing.md",
        "docs/spec/shipping.md",
        "docs/spec/order.md",
        "docs/spec/branches.md",
    )
    assert "docs/spec/open-questions.md" not in bundle_a.spec_files

    expected_sources = tuple(
        f"subject/src/main/java/shop/{p.name}"
        for p in sorted((generate_p3.ROOT / "subject/src/main/java/shop").glob("*.java"), key=lambda p: p.name)
    )
    assert bundle_a.source_files == expected_sources
    assert len(bundle_a.source_files) == 14

    # Verify canonical ordering inside rendered text.
    spec_positions = [bundle_a.text.index(f"### {rel}") for rel in bundle_a.spec_files[1:]]
    assert spec_positions == sorted(spec_positions)
    source_positions = [bundle_a.text.index(f"### {rel}") for rel in bundle_a.source_files]
    assert source_positions == sorted(source_positions)

    # Verify SHA-256 hashes match LF-normalized files.
    sys_lf = (generate_p3.ROOT / "prompts/direct/system.md").read_text(encoding="utf-8").replace("\r\n", "\n")
    gen_lf = (generate_p3.ROOT / "prompts/direct/generate.md").read_text(encoding="utf-8").replace("\r\n", "\n")
    assert bundle_a.system_sha256 == hashlib.sha256(sys_lf.encode("utf-8")).hexdigest()
    assert bundle_a.generate_sha256 == hashlib.sha256(gen_lf.encode("utf-8")).hexdigest()
    assert bundle_a.rendered_sha256 == hashlib.sha256(bundle_a.text.encode("utf-8")).hexdigest()


def test_build_prompt_missing_placeholder_raises(tmp_path):
    (tmp_path / "prompts/direct").mkdir(parents=True)
    (tmp_path / "prompts/direct/system.md").write_text("System rules\n", encoding="utf-8")
    (tmp_path / "prompts/direct/generate.md").write_text("Only {{API}} and {{SPEC}}\n", encoding="utf-8")
    (tmp_path / "docs/spec").mkdir(parents=True)
    (tmp_path / "docs/spec/api.md").write_text("API\n", encoding="utf-8")
    (tmp_path / "docs/spec/README.md").write_text("Spec\n", encoding="utf-8")
    (tmp_path / "subject/src/main/java/shop").mkdir(parents=True)
    (tmp_path / "subject/src/main/java/shop/Region.java").write_text("package shop; enum Region {}", encoding="utf-8")

    with pytest.raises(ValueError, match=r"\{\{SOURCE\}\}"):
        generate_p3.build_prompt(root=tmp_path)


def test_spec_only_uses_the_same_spec_without_subject_implementations():
    full = generate_p3.build_prompt()
    reduced = generate_p3.build_prompt(variant="spec-only")
    assert reduced.spec_files == full.spec_files
    assert reduced.source_files == ()
    assert reduced.chars < full.chars
    for rel in full.source_files:
        implementation = (generate_p3.ROOT / rel).read_text(encoding="utf-8").strip()
        assert implementation not in reduced.text
    for line in ('throw new IllegalArgumentException("Invalid shipping request");',
                 "case PROVINCE -> 35_000;", "fee += 2_000;",
                 "return List.copyOf(items.values());", "return product.getPrice() * quantity;",
                 "item.getProduct().restoreStock(item.getQuantity());"):
        assert line in full.text and line not in reduced.text


def test_dry_run_prints_prompt_and_char_count_without_calling_llm(monkeypatch, capsys):
    def fail_if_called(*args, **kwargs):
        raise AssertionError("LLM must not be called during --dry-run")

    monkeypatch.setattr(llm_client, "complete", fail_if_called)
    rc = generate_p3.main(["--run", "1", "--dry-run"])
    out = capsys.readouterr().out
    bundle = generate_p3.build_prompt()

    assert rc == 0
    assert bundle.text in out
    assert f"chars={bundle.chars}" in out
    assert f"system_sha256={bundle.system_sha256}" in out
    assert f"generate_sha256={bundle.generate_sha256}" in out


@pytest.mark.parametrize("bad_run", [0, -1, True])
def test_generate_rejects_invalid_run(bad_run):
    with pytest.raises(ValueError, match="run"):
        generate_p3.generate(bad_run)


@pytest.mark.parametrize("variant, method", [("full", "p3-direct-llm"), ("spec-only", "p3r-spec-only")])
def test_generate_applies_p3_timeout_override_and_writes_generation_json(workspace, monkeypatch, variant, method):
    raw_codex = (FIXTURES / "codex_ok.jsonl").read_text(encoding="utf-8")
    monkeypatch.setattr(llm_client, "ROOT", workspace)

    captured_subproc = {}

    def fake_subprocess_run(cmd, **kwargs):
        captured_subproc["cmd"] = cmd
        captured_subproc["timeout"] = kwargs["timeout"]
        captured_subproc["input_len"] = len(kwargs["input"])
        return subprocess.CompletedProcess(cmd, 0, stdout=raw_codex, stderr="")

    monkeypatch.setattr(llm_client.subprocess, "run", fake_subprocess_run)

    out_dir = workspace / "run-02"
    out_dir.mkdir()
    stale_java = out_dir / "Stale_999Test.java"
    stale_java.write_text("package shop; class Stale_999Test {}", encoding="utf-8")

    def fake_filter(blocks, *, class_prefix, start_index, root):
        assert class_prefix == "P3"
        assert start_index == 1
        assert len(blocks) == 1
        kept = [
            FilteredBlock(
                block=blocks[0],
                class_name="P3_001Test",
                source="package shop;\n\npublic class P3_001Test {\n    @Test\n    void onePlusOne() {}\n}\n",
            )
        ]
        dropped = [DroppedTest(name="dummyDropped", reason=REASON_COMPILE_ERROR)]
        return FilterResult(
            kept=kept,
            dropped=dropped,
            generated=2,
            compiled=1,
            passed_reference=1,
            valid=1,
            compile_rate=0.5,
            oracle_rate=1.0,
        )

    record = generate_p3.generate(
        2,
        variant=variant,
        provider="codex-cli",
        model="gpt-override",
        effort="high",
        out_dir=out_dir,
        filter_fn=fake_filter,
    )

    # p3.timeout_s from experiments/config/default.yaml is 1800 and must be applied to subprocess.run.
    assert captured_subproc["timeout"] == 1800
    assert "-m" in captured_subproc["cmd"] and "gpt-override" in captured_subproc["cmd"]
    assert "model_reasoning_effort=high" in captured_subproc["cmd"]

    # Stale .java removed, new test + generation.json written.
    assert not stale_java.exists()
    written_java = list(out_dir.glob("*.java"))
    assert [p.name for p in written_java] == ["P3_001Test.java"]

    meta_path = out_dir / "generation.json"
    assert meta_path.is_file()
    meta = json.loads(meta_path.read_text(encoding="utf-8"))
    assert meta == record
    assert meta["method"] == method
    assert (workspace / "results/llm-logs" / method / "run-02/calls.jsonl").is_file()
    assert meta["run"] == 2
    assert meta["iterations"] == []
    assert meta["llm"]["calls"] == 1
    assert meta["llm"]["provider"] == "codex-cli"
    assert meta["llm"]["model"] == "gpt-override"
    assert meta["llm"]["effort"] == "high"
    assert meta["llm"]["input_tokens"] == 22150
    assert meta["llm"]["output_tokens"] == 42

    assert meta["prompt"]["version"] == generate_p3.PROMPT_VERSION
    assert len(meta["prompt"]["system_sha256"]) == 64
    assert len(meta["prompt"]["generate_sha256"]) == 64
    assert len(meta["prompt"]["rendered_sha256"]) == 64
    assert meta["prompt"]["chars"] == captured_subproc["input_len"]

    assert meta["filter"] == {
        "generated": 2,
        "compiled": 1,
        "passed_reference": 1,
        "valid": 1,
        "compile_rate": 0.5,
        "oracle_correct_rate": 1.0,
        "dropped": [{"name": "dummyDropped", "reason": REASON_COMPILE_ERROR}],
    }

    # Compatible with measure.py helpers.
    assert measure.read_generation(out_dir, method, 2) == meta
    llm_m = measure.llm_metrics(
        meta,
        {"model": "default", "effort": "medium", "price_per_mtok_input": 2.0, "price_per_mtok_output": 10.0},
    )
    assert llm_m["calls"] == 1
    assert llm_m["model"] == "gpt-override"
    assert llm_m["effort"] == "high"
    assert llm_m["cost_usd"] == pytest.approx((22150 * 2.0 + 42 * 10.0) / 1_000_000)

    test_m = measure.test_metrics(meta, {"tests": 1, "skipped": 0}, 1, 0)
    assert test_m["generated"] == 2
    assert test_m["compiled"] == 1
    assert test_m["passed_reference"] == 1
    assert test_m["valid"] == 1
    assert test_m["compile_rate"] == pytest.approx(0.5)
    assert test_m["oracle_rate"] == pytest.approx(1.0)
    assert test_m["dropped"] == [{"name": "dummyDropped", "reason": REASON_COMPILE_ERROR}]


def test_cli_propagates_overrides_and_error_exit_codes(monkeypatch, capsys):
    seen = {}

    def fake_generate(run, *, variant, provider=None, model=None, effort=None):
        assert variant == "full"
        seen.update(run=run, provider=provider, model=model, effort=effort)
        return {
            "generation_s": 1.25,
            "llm": {"input_tokens": 1000, "output_tokens": 200},
            "filter": {
                "generated": 4,
                "compiled": 3,
                "passed_reference": 2,
                "valid": 1,
                "compile_rate": 0.75,
                "oracle_correct_rate": 2 / 3,
                "dropped": [],
            },
        }

    monkeypatch.setattr(generate_p3, "generate", fake_generate)
    rc = generate_p3.main(["--run", "3", "--provider", "agy-cli", "--model", "m1", "--effort", "low"])
    assert rc == 0
    assert seen == {"run": 3, "provider": "agy-cli", "model": "m1", "effort": "low"}
    out = capsys.readouterr().out
    assert "run-03:" in out and "compile_rate=75.0%" in out

    monkeypatch.setattr(generate_p3, "generate", lambda *a, **kw: (_ for _ in ()).throw(LlmQuotaExceeded("limit")))
    assert generate_p3.main(["--run", "1"]) == 2

    monkeypatch.setattr(generate_p3, "generate", lambda *a, **kw: (_ for _ in ()).throw(LlmError("boom")))
    assert generate_p3.main(["--run", "1"]) == 1


@pytest.mark.maven
def test_end_to_end_p3_real_maven_filter_with_mock_llm(workspace):
    """End-to-end P3 pipeline with mocked LLM returning 4 blocks (valid, compile error, wrong oracle, duplicate)."""
    sample_response = """
Below are four candidate tests:

```java
@Test
void validInnerCityShippingAtThreshold() {
    ShippingCalculator calc = new ShippingCalculator();
    assertEquals(0L, calc.calculate(Region.INNER_CITY, 1000, 500_000L));
    assertEquals(15_000L, calc.calculate(Region.INNER_CITY, 1000, 499_999L));
}
```

```java
@Test
void brokenCompileMethodCall() {
    Cart cart = new Cart();
    cart.methodThatDoesNotExist();
}
```

```java
@Test
void wrongOracleShippingFee() {
    ShippingCalculator calc = new ShippingCalculator();
    assertEquals(99_999L, calc.calculate(Region.REMOTE, 1000, 100_000L));
}
```

```java
@Test
void duplicateInnerCityShippingTest() {
    // Same body as validInnerCityShippingAtThreshold with extra spacing/comment
    ShippingCalculator calc = new ShippingCalculator();
    assertEquals(0L,   calc.calculate(Region.INNER_CITY, 1000, 500_000L));
    assertEquals(15_000L, calc.calculate(Region.INNER_CITY, 1000, 499_999L));
}
```
"""

    seen_call = {}

    def mock_complete(prompt, *, method, run, purpose, provider=None, model=None, effort=None):
        seen_call.update(
            prompt_len=len(prompt),
            method=method,
            run=run,
            purpose=purpose,
            provider=provider,
            model=model,
            effort=effort,
        )
        return LlmResult(
            provider=provider or "codex-cli",
            model=model or "mock-model",
            effort=effort or "medium",
            text=sample_response,
            input_tokens=15_000,
            cached_input_tokens=0,
            output_tokens=420,
            reasoning_output_tokens=80,
            tool_events=0,
            duration_s=1.5,
            cost_usd=None,
        )

    run_dir = workspace / "test-suites/p3-direct-llm/run-01"
    record = generate_p3.generate(
        1,
        provider="agy-cli",
        model="mock-gemini",
        effort="medium",
        out_dir=run_dir,
        complete_fn=mock_complete,
    )

    assert seen_call["method"] == "p3-direct-llm"
    assert seen_call["run"] == 1
    assert seen_call["purpose"] == "direct"
    assert seen_call["provider"] == "agy-cli"
    assert seen_call["model"] == "mock-gemini"
    assert seen_call["prompt_len"] == record["prompt"]["chars"]

    java_files = sorted(run_dir.glob("*.java"))
    assert [p.name for p in java_files] == ["P3_001Test.java"]
    saved_bytes = java_files[0].read_bytes()
    assert not saved_bytes.startswith(b"\xef\xbb\xbf")
    saved_src = java_files[0].read_text(encoding="utf-8")
    assert saved_src.startswith("package shop;\n")
    assert "void validInnerCityShippingAtThreshold()" in saved_src

    meta_path = run_dir / "generation.json"
    assert meta_path.is_file()
    meta = json.loads(meta_path.read_text(encoding="utf-8"))
    assert meta == record

    flt = meta["filter"]
    assert flt["generated"] == 4
    assert flt["compiled"] == 3
    assert flt["passed_reference"] == 2
    assert flt["valid"] == 1
    assert flt["compile_rate"] == pytest.approx(3 / 4)
    assert flt["oracle_correct_rate"] == pytest.approx(2 / 3)
    assert flt["dropped"] == [
        {"name": "brokenCompileMethodCall", "reason": REASON_COMPILE_ERROR},
        {"name": "wrongOracleShippingFee", "reason": REASON_FAILS_ON_REFERENCE},
        {"name": "duplicateInnerCityShippingTest", "reason": REASON_DUPLICATE},
    ]

    assert meta["tests"]["generated"] == 4
    assert meta["tests"]["compiled"] == 3
    assert meta["tests"]["passed_reference"] == 2
    assert meta["tests"]["valid"] == 1
    assert meta["tests"]["compile_rate"] == pytest.approx(3 / 4)
    assert meta["tests"]["oracle_rate"] == pytest.approx(2 / 3)
    assert meta["llm"]["provider"] == "agy-cli"
    assert meta["llm"]["model"] == "mock-gemini"
    assert meta["llm"]["calls"] == 1
    assert meta["llm"]["input_tokens"] == 15_000
    assert meta["llm"]["output_tokens"] == 420
    assert meta["generation_s"] > 0
    assert meta["iterations"] == []
