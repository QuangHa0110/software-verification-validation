"""Unit tests for experiments/scripts/run_experiment.py using mocked subprocesses."""

import json
from pathlib import Path
import shutil
import subprocess
from uuid import uuid4

import pytest

from experiments.scripts import run_experiment
from tools.svv.llm_client import LlmQuotaExceeded


@pytest.fixture
def workspace():
    path = run_experiment.ROOT / ".work" / f"test-runner-{uuid4().hex}"
    path.mkdir(parents=True)
    cfg = path / "experiments/config"
    cfg.mkdir(parents=True)
    (cfg / "default.yaml").write_text("runs: 3\nmeasure:\n  mutation: true\n", encoding="utf-8")
    # Create dummy scripts so non-dry-run tests see existing generator tools.
    for rel in (
        "tools/generate_p2.py",
        "tools/generate_p3.py",
        "tools/generate_p4.py",
        "experiments/scripts/measure.py",
    ):
        target = path / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text("# stub\n", encoding="utf-8")
    try:
        yield path
    finally:
        shutil.rmtree(path)


def test_parse_runs_and_methods():
    assert run_experiment.parse_runs(None, default_runs=4) == [1, 2, 3, 4]
    assert run_experiment.parse_runs("1-3") == [1, 2, 3]
    assert run_experiment.parse_runs("1,3-4,2") == [1, 2, 3, 4]
    with pytest.raises(ValueError):
        run_experiment.parse_runs("5-2")
    with pytest.raises(ValueError):
        run_experiment.parse_runs("0")

    assert "evosuite" not in run_experiment.parse_methods(None)
    assert run_experiment.parse_methods("p1-manual,evosuite") == ["p1-manual", "evosuite"]
    with pytest.raises(ValueError, match="Unknown method"):
        run_experiment.parse_methods("p9-unknown")


def test_dry_run_enforces_paired_order_and_skips_evosuite(workspace, capsys):
    # Deliberately scramble methods to verify paired ordering P3 -> P4 -> P4b per run.
    rc = run_experiment.main(
        [
            "--methods",
            "p4b-mutation-feedback,p3-direct-llm,evosuite,p4-coverage-guided,p1-manual,p2-random",
            "--runs",
            "1-2",
            "--dry-run",
        ],
        root=workspace,
    )
    assert rc == 0
    out = capsys.readouterr().out
    lines = [line for line in out.splitlines() if line and line[0].isdigit()]
    steps = [
        (parts[1].strip("[]"), parts[2], parts[3])
        for line in lines
        for parts in [line.split()]
    ]
    assert steps == [
        ("MEASURE", "p1-manual", "run-01"),
        ("GENERATE", "p2-random", "run-01"),
        ("MEASURE", "p2-random", "run-01"),
        ("GENERATE", "p2-random", "run-02"),
        ("MEASURE", "p2-random", "run-02"),
        ("GENERATE", "p3-direct-llm", "run-01"),
        ("MEASURE", "p3-direct-llm", "run-01"),
        ("GENERATE", "p4-coverage-guided", "run-01"),
        ("MEASURE", "p4-coverage-guided", "run-01"),
        ("GENERATE", "p4b-mutation-feedback", "run-01"),
        ("MEASURE", "p4b-mutation-feedback", "run-01"),
        ("GENERATE", "p3-direct-llm", "run-02"),
        ("MEASURE", "p3-direct-llm", "run-02"),
        ("GENERATE", "p4-coverage-guided", "run-02"),
        ("MEASURE", "p4-coverage-guided", "run-02"),
        ("GENERATE", "p4b-mutation-feedback", "run-02"),
        ("MEASURE", "p4b-mutation-feedback", "run-02"),
        ("SKIP", "evosuite", "-:"),
    ]
    assert "EvoSuite" in out
    assert not (workspace / "results/comparison/run-log.jsonl").exists()


def test_execute_plan_commands_overrides_and_log(workspace):
    plan = run_experiment.build_plan(
        ["p2-random", "p3-direct-llm", "p4-coverage-guided", "p4b-mutation-feedback"],
        [1],
        root=workspace,
        python_exe="python",
        provider="agy-cli",
        model="gemini-test",
        effort="high",
    )
    recorded: list[list[str]] = []

    def fake_runner(cmd, cwd):
        assert cwd == workspace
        recorded.append(list(cmd))
        return subprocess.CompletedProcess(cmd, 0, stdout="ok\n", stderr="")

    rc = run_experiment.execute_plan(
        plan,
        methods=["p2-random", "p3-direct-llm", "p4-coverage-guided", "p4b-mutation-feedback"],
        runs=[1],
        root=workspace,
        provider="agy-cli",
        model="gemini-test",
        effort="high",
        runner=fake_runner,
    )
    assert rc == 0
    assert len(recorded) == 8

    # P2 does not receive LLM overrides.
    assert recorded[0][1].endswith("generate_p2.py")
    assert "--provider" not in recorded[0]
    # Measure receives --mutation.
    assert recorded[1][1].endswith("measure.py")
    assert "--mutation" in recorded[1]
    # P3, P4, P4b receive LLM overrides; P4b includes --mutation-feedback.
    assert recorded[2][1].endswith("generate_p3.py")
    assert "--provider" in recorded[2] and "agy-cli" in recorded[2]
    assert recorded[4][1].endswith("generate_p4.py")
    assert "--mutation-feedback" not in recorded[4]
    assert recorded[6][1].endswith("generate_p4.py")
    assert "--mutation-feedback" in recorded[6]

    log_lines = (workspace / "results/comparison/run-log.jsonl").read_text(encoding="utf-8").splitlines()
    assert len(log_lines) == 8
    entries = [json.loads(line) for line in log_lines]
    assert all(e["status"] == "ok" and e["returncode"] == 0 for e in entries)
    assert [e["method"] for e in entries] == [
        "p2-random",
        "p2-random",
        "p3-direct-llm",
        "p3-direct-llm",
        "p4-coverage-guided",
        "p4-coverage-guided",
        "p4b-mutation-feedback",
        "p4b-mutation-feedback",
    ]


def test_skip_existing_and_skip_generation(workspace):
    # Pre-populate P3 run-01 generation.json and raw JSON; P4 run-01 generation.json only.
    p3_suite = workspace / "test-suites/p3-direct-llm/run-01"
    p3_suite.mkdir(parents=True)
    (p3_suite / "generation.json").write_text("{}", encoding="utf-8")
    p3_raw = workspace / "results/comparison/raw/p3-direct-llm/run-01.json"
    p3_raw.parent.mkdir(parents=True)
    p3_raw.write_text("{}", encoding="utf-8")

    p4_suite = workspace / "test-suites/p4-coverage-guided/run-01"
    p4_suite.mkdir(parents=True)
    (p4_suite / "generation.json").write_text("{}", encoding="utf-8")

    plan = run_experiment.build_plan(
        ["p3-direct-llm", "p4-coverage-guided"],
        [1],
        root=workspace,
        python_exe="python",
    )
    executed: list[tuple[str, str]] = []

    def fake_runner(cmd, cwd):
        executed.append((Path(cmd[1]).name, " ".join(cmd)))
        return subprocess.CompletedProcess(cmd, 0, stdout="", stderr="")

    rc = run_experiment.execute_plan(
        plan,
        methods=["p3-direct-llm", "p4-coverage-guided"],
        runs=[1],
        root=workspace,
        skip_existing=True,
        runner=fake_runner,
    )
    assert rc == 0
    # Only P4 measure should run!
    assert len(executed) == 1
    assert executed[0][0] == "measure.py"
    assert "p4-coverage-guided" in executed[0][1]

    # --skip-generation omits all generate steps from plan.
    measure_only = run_experiment.build_plan(
        ["p2-random", "p3-direct-llm"],
        [1, 2],
        root=workspace,
        skip_generation=True,
    )
    assert all(step.kind == "measure" for step in measure_only)


@pytest.mark.parametrize("mode", ["returncode_2", "stderr_text", "direct_exception"])
def test_quota_exceeded_stops_cleanly_and_prints_resume_command(workspace, capsys, mode):
    plan = run_experiment.build_plan(
        ["p3-direct-llm", "p4-coverage-guided"],
        [1, 2],
        root=workspace,
        python_exe="python",
        provider="codex-cli",
    )
    calls = 0

    def fake_runner(cmd, cwd):
        nonlocal calls
        calls += 1
        if calls == 3:  # P4 run-01 generate
            if mode == "direct_exception":
                raise LlmQuotaExceeded("usage limit reached")
            if mode == "returncode_2":
                return subprocess.CompletedProcess(cmd, 2, stdout="", stderr="Quota exceeded")
            return subprocess.CompletedProcess(cmd, 1, stdout="", stderr="LlmQuotaExceeded: rate limit")
        return subprocess.CompletedProcess(cmd, 0, stdout="", stderr="")

    rc = run_experiment.execute_plan(
        plan,
        methods=["p3-direct-llm", "p4-coverage-guided"],
        runs=[1, 2],
        root=workspace,
        provider="codex-cli",
        runner=fake_runner,
    )
    assert rc == run_experiment.QUOTA_EXIT_CODE
    assert calls == 3  # Stopped immediately; did not continue to P4 measure or run-02.
    err = capsys.readouterr().err
    assert "p4-coverage-guided" in err
    assert "run-01" in err
    assert "--skip-existing" in err
    assert "--provider codex-cli" in err

    entries = [
        json.loads(line)
        for line in (workspace / "results/comparison/run-log.jsonl").read_text(encoding="utf-8").splitlines()
    ]
    assert entries[-1]["status"] == "quota_exceeded"
    assert entries[-1]["method"] == "p4-coverage-guided"
    assert entries[-1]["run"] == 1


def test_argparse_error_exit_2_is_not_misclassified_as_quota(workspace, capsys):
    plan = run_experiment.build_plan(["p3-direct-llm"], [1], root=workspace, python_exe="python")

    def fake_runner(cmd, cwd):
        return subprocess.CompletedProcess(
            cmd, 2, stdout="", stderr="usage: generate_p3.py [--run RUN]\ngenerate_p3.py: error: unrecognized arguments"
        )

    rc = run_experiment.execute_plan(plan, methods=["p3-direct-llm"], runs=[1], root=workspace, runner=fake_runner)
    assert rc == 1
    assert "Step failed" in capsys.readouterr().err


def test_missing_generator_script_is_skipped_cleanly(workspace, capsys):
    (workspace / "tools/generate_p3.py").unlink()
    plan = run_experiment.build_plan(["p3-direct-llm"], [1], root=workspace, python_exe="python")
    rc = run_experiment.execute_plan(
        plan,
        methods=["p3-direct-llm"],
        runs=[1],
        root=workspace,
        runner=lambda cmd, cwd: pytest.fail("Should not invoke missing generator or measure without suite"),
    )
    assert rc == 0
    out = capsys.readouterr().out
    assert "[SKIP]" in out
    entries = [
        json.loads(line)
        for line in (workspace / "results/comparison/run-log.jsonl").read_text(encoding="utf-8").splitlines()
    ]
    assert [e["status"] for e in entries] == ["skipped", "skipped"]
