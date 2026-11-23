import json
from pathlib import Path
import shutil

import pytest
from jsonschema import Draft202012Validator

from tools import generate_p4 as p4
from tools.svv.config import ROOT
from tools.svv.selection import Measurement


def method(name, body):
    return f"```java\n@Test\nvoid {name}() {{\n    {body}\n}}\n```"


@pytest.fixture
def repo(monkeypatch):
    # Keep even the nested Maven projects and PIT command lines short on Windows.
    with p4._stage_dir(ROOT, "t4-") as root:
        for subdir in ("docs/spec", "prompts", "subject", "reference", "experiments/config"):
            shutil.copytree(ROOT / subdir, root / subdir, ignore=shutil.ignore_patterns("target", "*.local.yaml"))
        eq = Path("results/mutation/equivalent-mutants.csv")
        (root / eq.parent).mkdir(parents=True)
        shutil.copy2(ROOT / eq, root / eq)
        monkeypatch.setattr(p4, "ROOT", root)
        monkeypatch.setattr(p4.llm_client, "ROOT", root)

        def unexpected(*args, **kwargs):
            pytest.fail("Unexpected provider call")

        for backend in ("_codex", "_agy", "_anthropic"):
            monkeypatch.setattr(p4.llm_client, backend, unexpected)
        yield root


def seed(root, *, paired=False):
    rel = Path("test-suites") / p4.P3 / "run-01"
    suite = root / rel
    if paired and (ROOT / rel).is_dir():
        shutil.copytree(ROOT / rel, suite)
    else:
        suite.mkdir(parents=True)
        shutil.copy2(ROOT / "test-suites/smoke/DomainSmokeTest.java", suite)
        meta = {"method": p4.P3, "run": 1, "generation_s": 7.0,
                "llm": {"calls": 1, "input_tokens": 600, "output_tokens": 50, "cost_usd": None},
                "tests": dict.fromkeys(p4.TEST_COUNTS, 5)}
        (suite / "generation.json").write_text(json.dumps(meta), encoding="utf-8")
    return suite


def fake_provider(monkeypatch, responses):
    prompts = []
    responses = iter(responses)

    def complete(prompt, config, result):
        assert config["timeout_s"] == 1800
        prompts.append(prompt)
        result.input_tokens, result.output_tokens = 20, 4
        result.cached_input_tokens, result.reasoning_output_tokens = 6, 2
        result.text = next(responses)

    monkeypatch.setattr(p4.llm_client, "_codex", complete)
    return prompts


def observed(branch=25):
    return Measurement({kind: {"pct": branch, "covered": branch, "total": 100}
                        for kind in ("statement", "branch")}, {"lines": [], "branches": []})


def read_record(suite):
    return json.loads((suite / "generation.json").read_text(encoding="utf-8"))


@pytest.mark.parametrize("mutation_score", [None, 71.5])
def test_iteration_schema(mutation_score):
    measurement = observed()
    measurement.mutation_score = mutation_score
    iteration = {"i": 1, **measurement.metrics(), "kept_tests": 1, "tokens": 24,
                 "generated": 2, "valid": 1, "dropped_by_reason": {"duplicate": 1}}
    schema_path = ROOT / "experiments/schema/run-result.schema.json"
    schema = json.loads(schema_path.read_text(encoding="utf-8"))
    Draft202012Validator(schema).evolve(schema=schema["$defs"]["iteration"]).validate(iteration)


def test_missing_paired_input_fails_early_for_both_methods(repo, capsys):
    for flags, parent in (([], p4.P3), (["--mutation-feedback"], p4.P4)):
        assert p4.main(["--run", "2", *flags]) == 1
        assert f"Missing paired {parent} run-02" in capsys.readouterr().err
    assert not (repo / "test-suites").exists()


@pytest.mark.parametrize("override, branch, reason, calls", [
    ("{}", 100, "target_branch_coverage", 0),
    ("{demo_max_iterations: 1}", 25, "max_iterations", 1),
    ("{token_budget: 24}", 25, "token_budget", 1),
    ("{token_budget: 23}", 25, "token_budget", 1),
    ("{stop_if_no_gain_iterations: 2}", 25, "stop_if_no_gain_iterations", 2),
])
def test_stops_accounting_demo_and_cli_overrides(repo, monkeypatch, capsys, override, branch, reason, calls):
    parent = seed(repo)
    (repo / "experiments/config/check.local.yaml").write_text(f"p4: {override}", encoding="utf-8")
    monkeypatch.setattr(p4, "measure_suite", lambda *args, **kwargs: observed(branch))
    prompts = fake_provider(monkeypatch, [""] * calls)
    assert p4.main(["--run", "1", "--demo", "--provider", "codex-cli", "--model", "mock",
                    "--effort", "high"]) == 0
    suite = repo / ".work/demo" / p4.P4 / "run-01"
    rec = read_record(suite)
    assert rec["stop_reason"] == reason
    assert rec["baseline"] == rec["final"] and rec["final"]["branch_pct"] == branch
    assert len(prompts) == len(rec["iterations"]) == calls
    assert rec["llm"]["input_tokens"] == 600 + calls * 20
    assert rec["llm"]["output_tokens"] == 50 + calls * 4
    assert rec["llm"]["total_tokens"] == 650 + calls * 24
    assert rec["incremental"]["llm"]["total_tokens"] == calls * 24
    assert rec["llm"]["calls"] == calls + 1
    assert rec["generation_s"] == 7 + rec["incremental"]["generation_s"]
    assert (parent / "DomainSmokeTest.java").read_bytes() == (suite / "DomainSmokeTest.java").read_bytes()
    assert not (repo / "test-suites" / p4.P4).exists()
    if calls:
        assert "branch_pct=25.00 -> 25.00, kept_tests=0" in capsys.readouterr().out
        assert rec["llm"]["model"] == "mock" and rec["llm"]["effort"] == "high"
        logs = (repo / "results/llm-logs" / p4.P4 / "run-01/calls.jsonl").read_text(encoding="utf-8")
        assert [json.loads(line)["prompt"] for line in logs.splitlines()] == prompts
        names = prompts[0].split("## Tên phương thức test hiện có\n\n")[1].split("\n\n##")[0]
        assert names.splitlines()[0] == "productKeepsItsFieldsAndAdjustsCurrentStock"
        assert len(names.splitlines()) == 5


def test_demo_limit_output_override_replay_and_quota_preserve_previous_suite(repo, monkeypatch, capsys):
    seed(repo)
    (repo / "experiments/config/check.local.yaml").write_text("p4: {demo_max_iterations: 1}", encoding="utf-8")
    monkeypatch.setattr(p4, "measure_suite", lambda *args, **kwargs: observed())
    dest = repo / ".work/custom-output"
    cmd = ["--run", "1", "--output", str(dest), "--demo"]
    assert p4.main([*cmd, "--dry-run"]) == 0
    assert "[dry-run] method=p4-coverage-guided run-01" in capsys.readouterr().out
    assert not dest.exists()
    fake_provider(monkeypatch, ["", ""])
    assert p4.main(cmd) == p4.main(cmd) == 0
    rec = read_record(dest)
    assert rec["stop_reason"] == "max_iterations" and len(rec["iterations"]) == 1

    def quota(*args):
        raise p4.llm_client.LlmQuotaExceeded("fixture quota exceeded")

    monkeypatch.setattr(p4.llm_client, "_codex", quota)
    assert p4.main(cmd) == 2
    assert read_record(dest) == rec


@pytest.mark.parametrize("score, iters, tokens, no_gain, expected", [
    (100, 0, 0, 0, "target_mutation_score"),
    (99, 3, 0, 0, "max_iterations"),
    (99, 1, 300000, 0, "token_budget"),
    (99, 2, 0, 2, "stop_if_no_gain_iterations"),
    (99, 1, 0, 1, None),
])
def test_p4b_limits_use_its_score_and_own_budget(score, iters, tokens, no_gain, expected):
    limits = p4.load_config(ROOT / "experiments/config")["p4"]["p4b"]
    assert p4.stopping_reason(limits, {"branch_pct": 100, "mutation_score": score}, mutation=True,
                              iterations=iters, tokens=tokens, no_gain=no_gain) == expected


@pytest.mark.maven
def test_paired_p4_then_p4b_with_mock_llm_real_maven(repo, monkeypatch, capsys):
    parent = seed(repo, paired=True)
    (repo / "experiments/config/check.local.yaml").write_text(
        "p4: {max_iterations: 2, p4b: {max_iterations: 1}}", encoding="utf-8")
    covering = method("innerCityBaseFee", "assertEquals(15000L, new ShippingCalculator()"
                      ".calculate(Region.INNER_CITY, 500, 1));")
    useless = method("constantAssertion", "assertTrue(17 > 0);")
    killer = method("newCartReportsEmpty", "assertTrue(new Cart().isEmpty());")
    prompts = fake_provider(monkeypatch, [f"{useless}\n{covering}", covering, killer])
    assert p4.main(["--run", "1", "--dry-run"]) == 0
    preview = capsys.readouterr().out
    assert "branches covered=" in preview and not prompts
    assert not (repo / "results/llm-logs").exists()
    assert p4.main(["--run", "1"]) == 0
    suite = repo / "test-suites" / p4.P4 / "run-01"
    rec = read_record(suite)
    first, second = rec["iterations"]
    assert (first["generated"], first["valid"], first["kept_tests"]) == (2, 2, 1)
    assert first["dropped_by_reason"] == {"no_coverage_gain": 1}
    assert rec["incremental"]["dropped_by_selection"] == [
        {"i": 1, "name": "constantAssertion", "reason": "no_coverage_gain"}]
    assert second["kept_tests"] == 0 and second["dropped_by_reason"] == {"duplicate": 1}
    assert first["branch_pct"] > rec["baseline"]["branch_pct"]
    assert second["branch_pct"] == first["branch_pct"]
    assert second["statement_pct"] == first["statement_pct"] >= rec["baseline"]["statement_pct"]
    assert not (suite / "P4_001Test.java").exists()
    assert "innerCityBaseFee" in prompts[1] and "P4_002Test.java" in rec["suite_files"]
    parent_rec = read_record(parent)
    assert rec["llm"]["input_tokens"] == parent_rec["llm"]["input_tokens"] + 40
    assert rec["llm"]["output_tokens"] == parent_rec["llm"]["output_tokens"] + 8
    assert p4.main(["--run", "1", "--mutation-feedback"]) == 0
    final = repo / "test-suites" / p4.P4B / "run-01"
    mut = read_record(final)
    iteration, = mut["iterations"]
    assert iteration["kept_tests"] == 1 and iteration["mutation_score"] > mut["baseline"]["mutation_score"]
    assert "P4b_001Test.java" in mut["suite_files"]
    assert "pit " in prompts[2] and "custom " in prompts[2] and "replacement:" in prompts[2]
    assert mut["llm"]["calls"] == parent_rec["llm"]["calls"] + 3
    assert mut["llm"]["input_tokens"] == rec["llm"]["input_tokens"] + 20
    assert mut["llm"]["output_tokens"] == rec["llm"]["output_tokens"] + 4
    assert mut["generation_s"] == rec["generation_s"] + mut["incremental"]["generation_s"]
    for src, dst in ((parent, suite), (suite, final)):
        for path in src.rglob("*.java"):
            assert (dst / path.relative_to(src)).read_bytes() == path.read_bytes()
    with capsys.disabled():
        print(f"\nP4 branch {rec['baseline']['branch_pct']:.2f} -> {first['branch_pct']:.2f} -> "
              f"{second['branch_pct']:.2f}, kept=[1, 0], dropped={first['dropped_by_reason']}, "
              f"{second['dropped_by_reason']}")
        print(f"P4b mutation {mut['baseline']['mutation_score']:.2f} -> {iteration['mutation_score']:.2f}, "
              "kept=[1] (PIT + custom)")
