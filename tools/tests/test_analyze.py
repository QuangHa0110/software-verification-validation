"""Unit tests for experiments/scripts/analyze.py using schema-valid synthetic fixtures."""

import csv
import json
from pathlib import Path
import shutil
from uuid import uuid4

from jsonschema import Draft202012Validator
import pytest

from experiments.scripts import analyze

SCHEMA = json.loads((analyze.ROOT / "experiments/schema/run-result.schema.json").read_text(encoding="utf-8"))
VALIDATOR = Draft202012Validator(SCHEMA)
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"


@pytest.fixture
def workspace():
    path = analyze.ROOT / ".work" / f"test-analyze-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def _make_run(
    method: str,
    run: int,
    *,
    stmt_pct: float,
    branch_pct: float,
    func_pct: float,
    mut_score: float,
    generated: int,
    compiled: int,
    valid: int,
    passed_reference: int | None = None,
    gen_s: float = 1.0,
    exec_s: float = 5.0,
    calls: int = 0,
    in_tok: int = 0,
    out_tok: int = 0,
    cost_usd: float | None = 0.0,
    iterations: list[dict] | None = None,
) -> dict:
    tests: dict = {
        "generated": generated,
        "compiled": compiled,
        "valid": valid,
        "passed": valid,
        "failed": 0,
        "skipped": 0,
        "dropped": [],
        "compile_rate": compiled / generated if generated else None,
    }
    if passed_reference is not None:
        tests["passed_reference"] = passed_reference
        tests["oracle_rate"] = passed_reference / compiled if compiled else None

    killed = int(round(mut_score))
    record: dict = {
        "method": method,
        "run": run,
        "tests": tests,
        "coverage": {
            "statement": {"covered": int(round(stmt_pct)), "total": 100, "pct": stmt_pct},
            "branch": {"covered": int(round(branch_pct)), "total": 100, "pct": branch_pct},
            "function": {"covered": int(round(func_pct)), "total": 100, "pct": func_pct},
        },
        "uncovered": {"lines": [], "branches": []},
        "mutation": {
            "total": 100,
            "killed": killed,
            "survived": 100 - killed,
            "no_coverage": 0,
            "timed_out": 0,
            "equivalent": 0,
            "score": mut_score,
            "by_operator": {
                "relational": {"total": 20, "killed": 15, "source": "pit"},
                "arithmetic": {"total": 20, "killed": 15, "source": "pit"},
                "conditional": {"total": 20, "killed": 15, "source": "custom"},
                "boolean_constant": {"total": 20, "killed": 15, "source": ["pit", "custom"]},
                "statement_deletion": {"total": 20, "killed": 15, "source": ["pit", "custom"]},
            },
        },
        "time": {"generation_s": gen_s, "execution_s": exec_s},
        "llm": {
            "calls": calls,
            "input_tokens": in_tok,
            "output_tokens": out_tok,
            "cost_usd": cost_usd,
            "model": "mock-model" if calls else "",
            "effort": "medium" if calls else "",
        },
    }
    if method in ("p4-coverage-guided", "p4b-mutation-feedback"):
        record["iterations"] = iterations if iterations is not None else []
    VALIDATOR.validate(record)
    return record


def _write_raw(raw_dir: Path, record: dict) -> None:
    target = raw_dir / record["method"] / f"run-{record['run']:02d}.json"
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(record, indent=2) + "\n", encoding="utf-8")


def _read_csv(path: Path) -> list[dict[str, str]]:
    raw_bytes = path.read_bytes()
    assert not raw_bytes.startswith(b"\xef\xbb\xbf"), f"Unexpected BOM in {path}"
    with path.open("r", encoding="utf-8", newline="") as handle:
        return list(csv.DictReader(handle))


def test_analyze_generates_all_tables_and_figures(workspace):
    raw_dir = workspace / "raw"
    out_dir = workspace / "out"

    # P1: single run
    _write_raw(
        raw_dir,
        _make_run(
            "p1-manual",
            1,
            stmt_pct=100.0,
            branch_pct=100.0,
            func_pct=100.0,
            mut_score=95.0,
            generated=70,
            compiled=70,
            valid=70,
            gen_s=0.0,
            exec_s=12.0,
        ),
    )

    # P2, P3, P4, P4b: 3 paired runs each
    for r in (1, 2, 3):
        _write_raw(
            raw_dir,
            _make_run(
                "p2-random",
                r,
                stmt_pct=65.0 + r,
                branch_pct=55.0 + r,
                func_pct=80.0 + r,
                mut_score=50.0 + 2 * r,
                generated=100,
                compiled=100,
                valid=100,
                gen_s=0.2,
                exec_s=10.0,
            ),
        )
        _write_raw(
            raw_dir,
            _make_run(
                "p3-direct-llm",
                r,
                stmt_pct=82.0 + r,
                branch_pct=75.0 + r,
                func_pct=92.0 + r,
                mut_score=74.0 + r,
                generated=40,
                compiled=36,
                valid=32,
                passed_reference=34,
                gen_s=25.0 + r,
                exec_s=11.0,
                calls=1,
                in_tok=25000,
                out_tok=4000,
                cost_usd=None,
            ),
        )
        _write_raw(
            raw_dir,
            _make_run(
                "p4-coverage-guided",
                r,
                stmt_pct=93.0 + r,
                branch_pct=89.0 + r,
                func_pct=96.0 + r,
                mut_score=84.0 + r,
                generated=55,
                compiled=50,
                valid=42,
                passed_reference=48,
                gen_s=60.0 + r,
                exec_s=13.0,
                calls=3,
                in_tok=75000,
                out_tok=10000,
                cost_usd=None,
                iterations=[
                    {"i": 0, "statement_pct": 82.0 + r, "branch_pct": 75.0 + r, "kept_tests": 32, "tokens": 29000},
                    {"i": 1, "statement_pct": 89.0 + r, "branch_pct": 84.0 + r, "kept_tests": 6, "tokens": 28000},
                    {"i": 2, "statement_pct": 93.0 + r, "branch_pct": 89.0 + r, "kept_tests": 4, "tokens": 28000},
                ],
            ),
        )
        _write_raw(
            raw_dir,
            _make_run(
                "p4b-mutation-feedback",
                r,
                stmt_pct=95.0 + r,
                branch_pct=92.0 + r,
                func_pct=97.0 + r,
                mut_score=91.0 + r,
                generated=62,
                compiled=58,
                valid=48,
                passed_reference=56,
                gen_s=85.0 + r,
                exec_s=14.0,
                calls=5,
                in_tok=120000,
                out_tok=15000,
                cost_usd=None,
                iterations=[
                    {"i": 0, "statement_pct": 93.0 + r, "branch_pct": 89.0 + r, "kept_tests": 42, "tokens": 85000},
                    {"i": 1, "statement_pct": 95.0 + r, "branch_pct": 92.0 + r, "kept_tests": 6, "tokens": 50000},
                ],
            ),
        )

    rc = analyze.main(["--raw-dir", str(raw_dir), "--out-dir", str(out_dir)])
    assert rc == 0

    # 1. Summary CSV & MD
    summary_rows = _read_csv(out_dir / "summary.csv")
    summary_index = {(row["method"], row["metric"]): row for row in summary_rows}
    assert summary_index[("p1-manual", "branch_pct")]["n"] == "1"
    assert summary_index[("p1-manual", "branch_pct")]["std"] == ""
    assert float(summary_index[("p1-manual", "branch_pct")]["mean"]) == pytest.approx(100.0)
    assert summary_index[("p3-direct-llm", "cost_usd")]["n"] == "0"
    assert float(summary_index[("p3-direct-llm", "compile_rate")]["mean"]) == pytest.approx(36 / 40)
    assert float(summary_index[("p3-direct-llm", "oracle_rate")]["mean"]) == pytest.approx(34 / 36)
    assert (out_dir / "summary.md").is_file()

    # 2. Pairwise CSV & MD (no P1, no subjective wording)
    pairwise_rows = _read_csv(out_dir / "pairwise.csv")
    pairs = {row["pair"] for row in pairwise_rows}
    assert pairs == {
        "p4-coverage-guided vs p3-direct-llm",
        "p3-direct-llm vs p2-random",
        "p4-coverage-guided vs p2-random",
        "p4b-mutation-feedback vs p4-coverage-guided",
    }
    assert all("p1-manual" not in row["pair"] for row in pairwise_rows)
    pw_index = {(row["pair"], row["metric"]): row for row in pairwise_rows}
    p4_vs_p3_branch = pw_index[("p4-coverage-guided vs p3-direct-llm", "branch_pct")]
    assert float(p4_vs_p3_branch["a12"]) == pytest.approx(1.0)
    assert p4_vs_p3_branch["effect_size"] == "large"
    assert float(p4_vs_p3_branch["p_value"]) > 0.0

    pairwise_md = (out_dir / "pairwise.md").read_text(encoding="utf-8").lower()
    assert "tốt hơn" not in pairwise_md
    assert "better" not in pairwise_md

    # 3. Paired coverage before/after
    ba_rows = _read_csv(out_dir / "coverage-before-after.csv")
    assert [row["run"] for row in ba_rows] == ["run-01", "run-02", "run-03"]
    assert float(ba_rows[0]["delta_p4_p3_branch_pct"]) == pytest.approx(14.0)
    assert float(ba_rows[0]["delta_p4b_p4_mutation_score"]) == pytest.approx(7.0)
    assert (out_dir / "coverage-before-after.md").is_file()

    # 4. Iterations
    iter_rows = _read_csv(out_dir / "iterations.csv")
    assert len(iter_rows) == 3 * 3 + 3 * 2  # 3 runs * 3 iters (P4) + 3 runs * 2 iters (P4b)

    # 5. RQ5 Spearman correlation
    corr_rows = _read_csv(out_dir / "rq5-correlation.csv")
    corr_index = {(row["var_x"], row["var_y"]): row for row in corr_rows}
    assert corr_index[("branch_pct", "mutation_score")]["n"] == "13"
    assert float(corr_index[("branch_pct", "mutation_score")]["spearman_rho"]) > 0.9

    # 6. Figures
    fig_dir = out_dir / "figures"
    for name in (
        "coverage_boxplot.png",
        "mutation_boxplot.png",
        "p4_iterations_coverage.png",
        "tests_vs_mutation_scatter.png",
    ):
        fig_path = fig_dir / name
        assert fig_path.is_file()
        assert fig_path.read_bytes().startswith(PNG_SIGNATURE)


def test_reduced_reports_join_same_run_and_restore_generated_iterations(workspace):
    raw = workspace / "results/comparison/raw"
    out = workspace / "out"
    p3r, p4r, p4br = analyze.REDUCED_CHAIN
    for method, run, pct in ((p3r, 1, 70), (p4r, 1, 80), (p4br, 1, 95), (p3r, 2, 72),
                             ("p3-direct-llm", 1, 100)):
        rec = _make_run(method, run, stmt_pct=pct, branch_pct=pct, func_pct=pct, mut_score=pct,
                        generated=3, compiled=3, valid=3)
        _write_raw(raw, rec)
        if method in (p4r, p4br):
            trace = workspace / "test-suites" / method / "run-01/generation.json"
            trace.parent.mkdir(parents=True)
            iteration = {"i": 1, "statement_pct": pct, "branch_pct": pct, "kept_tests": 1, "tokens": 100}
            if method == p4br:
                iteration["mutation_score"] = pct
            trace.write_text(json.dumps({"method": method, "run": run, "generation_s": rec["time"]["generation_s"],
                                         "baseline": {"branch_pct": 70, "statement_pct": 70, "mutation_score": 80},
                                         "iterations": [iteration]}), encoding="utf-8")
    outputs = analyze.analyze(raw_dir=raw, out_dir=out)
    rows = _read_csv(outputs["reduced_before_after_csv"])
    assert float(rows[0]["delta_p4r_p3r_branch_pct"]) == 10
    assert float(rows[0]["delta_p4br_p4r_mutation_score"]) == 15
    assert rows[1]["run"] == "run-02" and rows[1]["delta_p4r_p3r_branch_pct"] == ""
    pairs = {(r["method_a"], r["method_b"]) for r in _read_csv(outputs["pairwise_csv"])}
    assert {(p4r, p3r), (p4br, p4r), (p3r, "p3-direct-llm")} <= pairs
    iterations = _read_csv(outputs["iterations_csv"])
    assert [r["method"] for r in iterations] == [p4r, p4br]
    assert iterations[0]["mutation_score"] == "" and float(iterations[1]["mutation_score"]) == 95
    for figure in ("p4r_iterations_coverage", "p4br_iterations_mutation"):
        assert outputs[figure].read_bytes().startswith(PNG_SIGNATURE)


def test_analyze_handles_single_method_missing_data_without_fabricating(workspace):
    raw_dir = workspace / "raw"
    out_dir = workspace / "out"
    _write_raw(
        raw_dir,
        _make_run(
            "p1-manual",
            1,
            stmt_pct=98.0,
            branch_pct=97.0,
            func_pct=100.0,
            mut_score=91.5,
            generated=60,
            compiled=60,
            valid=60,
        ),
    )

    outputs = analyze.analyze(raw_dir=raw_dir, out_dir=out_dir)
    assert len(outputs) == 12

    summary_rows = _read_csv(out_dir / "summary.csv")
    summary_index = {(row["method"], row["metric"]): row for row in summary_rows}
    assert summary_index[("p1-manual", "statement_pct")]["n"] == "1"
    assert summary_index[("p1-manual", "oracle_rate")]["n"] == "0"
    assert summary_index[("p2-random", "statement_pct")]["n"] == "0"
    assert summary_index[("p2-random", "statement_pct")]["mean"] == ""

    pairwise_rows = _read_csv(out_dir / "pairwise.csv")
    assert all(row["u_statistic"] == "" and row["a12"] == "" for row in pairwise_rows)

    ba_rows = _read_csv(out_dir / "coverage-before-after.csv")
    assert ba_rows == []

    corr_rows = _read_csv(out_dir / "rq5-correlation.csv")
    assert all(row["n"] == "1" and row["spearman_rho"] == "" for row in corr_rows)

    for fig_name in (
        "coverage_boxplot.png",
        "mutation_boxplot.png",
        "p4_iterations_coverage.png",
        "tests_vs_mutation_scatter.png",
    ):
        assert (out_dir / "figures" / fig_name).read_bytes().startswith(PNG_SIGNATURE)
