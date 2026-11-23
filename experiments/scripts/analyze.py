"""Aggregate experiment raw JSON results into CSV/Markdown tables and matplotlib figures."""

from __future__ import annotations

import argparse
from collections.abc import Callable, Sequence
import csv
import json
from pathlib import Path
import sys
from typing import Any

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np

ROOT = Path(__file__).resolve().parents[2]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from tools.svv.pit import alive_mutants, load_equivalents, merge_mutations, read_suite_results, rescore
from tools.svv.stats import compare_samples, describe, spearman_correlation

CANONICAL_METHODS = (
    "p1-manual",
    "p2-random",
    "p3-direct-llm",
    "p4-coverage-guided",
    "p4b-mutation-feedback",
)
REDUCED_CHAIN = ("p3r-spec-only", "p4r-coverage-guided", "p4br-mutation-feedback")
WEAK_CHAIN = ("p3w-weak-llm", "p4w-coverage-guided", "p4bw-mutation-feedback")
VARIANT_CHAINS = {"reduced": REDUCED_CHAIN, "weak-llm": WEAK_CHAIN}
VARIANT_PARENTS = {child: parent for chain in VARIANT_CHAINS.values()
                   for parent, child in zip(chain, chain[1:])}

METHOD_LABELS = {
    "p1-manual": "P1 Manual",
    "p2-random": "P2 Random",
    "p3-direct-llm": "P3 Direct LLM",
    "p4-coverage-guided": "P4 Coverage",
    "p4b-mutation-feedback": "P4b Mutation",
    "p3r-spec-only": "P3r Spec-only",
    "p4r-coverage-guided": "P4r Coverage",
    "p4br-mutation-feedback": "P4br Mutation",
    "p3w-weak-llm": "P3w Weak LLM",
    "p4w-coverage-guided": "P4w Coverage",
    "p4bw-mutation-feedback": "P4bw Mutation",
    "evosuite": "EvoSuite",
}

PAIRWISE_COMPARISONS: tuple[tuple[str, str, str], ...] = (
    ("p4-coverage-guided", "p3-direct-llm", "RQ2, RQ4"),
    ("p3-direct-llm", "p2-random", "RQ3"),
    ("p4-coverage-guided", "p2-random", "RQ3"),
    ("p4b-mutation-feedback", "p4-coverage-guided", "P4b vs P4"),
)
REDUCED_COMPARISONS = (
    (REDUCED_CHAIN[1], REDUCED_CHAIN[0], "P4r vs P3r"),
    (REDUCED_CHAIN[2], REDUCED_CHAIN[1], "P4br vs P4r"),
    (REDUCED_CHAIN[0], "p3-direct-llm", "P3r vs P3"),
)
WEAK_COMPARISONS = (
    (WEAK_CHAIN[1], WEAK_CHAIN[0], "P4w vs P3w"),
    (WEAK_CHAIN[2], WEAK_CHAIN[1], "P4bw vs P4w"),
    (WEAK_CHAIN[0], "p3-direct-llm", "P3w vs P3"),
)


def _get_compile_rate(rec: dict[str, Any]) -> float | None:
    tests = rec.get("tests") or {}
    if "compile_rate" in tests:
        return tests["compile_rate"]
    generated = tests.get("generated")
    compiled = tests.get("compiled")
    if isinstance(generated, (int, float)) and generated > 0 and isinstance(compiled, (int, float)):
        return float(compiled) / float(generated)
    return None


def _get_oracle_rate(rec: dict[str, Any]) -> float | None:
    tests = rec.get("tests") or {}
    if "oracle_rate" in tests:
        return tests["oracle_rate"]
    compiled = tests.get("compiled")
    passed_ref = tests.get("passed_reference")
    if isinstance(compiled, (int, float)) and compiled > 0 and isinstance(passed_ref, (int, float)):
        return float(passed_ref) / float(compiled)
    return None


def _get_mutation_score(rec: dict[str, Any]) -> float | None:
    mutation = rec.get("mutation")
    if not isinstance(mutation, dict):
        return None
    return mutation.get("score")


METRICS: tuple[tuple[str, str, str, Callable[[dict[str, Any]], float | int | None]], ...] = (
    ("statement_pct", "Statement/Line coverage", "%", lambda r: r["coverage"]["statement"]["pct"]),
    ("branch_pct", "Branch coverage", "%", lambda r: r["coverage"]["branch"]["pct"]),
    ("function_pct", "Function coverage", "%", lambda r: r["coverage"]["function"]["pct"]),
    ("mutation_score", "Mutation score", "%", _get_mutation_score),
    ("tests_valid", "Valid tests", "count", lambda r: r["tests"]["valid"]),
    ("tests_generated", "Generated tests", "count", lambda r: r["tests"]["generated"]),
    ("execution_s", "Execution time", "s", lambda r: r["time"]["execution_s"]),
    ("generation_s", "Generation time", "s", lambda r: r["time"]["generation_s"]),
    ("input_tokens", "Input tokens", "tokens", lambda r: r["llm"]["input_tokens"]),
    ("output_tokens", "Output tokens", "tokens", lambda r: r["llm"]["output_tokens"]),
    ("cost_usd", "LLM cost", "USD", lambda r: r["llm"].get("cost_usd")),
    ("compile_rate", "Compile rate", "ratio", _get_compile_rate),
    ("oracle_rate", "Oracle correctness rate", "ratio", _get_oracle_rate),
)

RQ5_CORRELATION_PAIRS: tuple[tuple[str, str], ...] = (
    ("tests_valid", "statement_pct"),
    ("tests_valid", "branch_pct"),
    ("tests_valid", "function_pct"),
    ("tests_valid", "mutation_score"),
    ("statement_pct", "mutation_score"),
    ("branch_pct", "mutation_score"),
    ("function_pct", "mutation_score"),
    ("statement_pct", "branch_pct"),
)


def _fmt_csv(value: Any, digits: int = 6) -> str:
    if value is None:
        return ""
    if isinstance(value, bool):
        return str(value)
    if isinstance(value, int):
        return str(value)
    if isinstance(value, float):
        return f"{value:.{digits}f}".rstrip("0").rstrip(".") if value != 0 else "0"
    return str(value)


def _fmt_md(value: Any, digits: int = 4) -> str:
    if value is None:
        return "—"
    if isinstance(value, int):
        return str(value)
    if isinstance(value, float):
        return f"{value:.{digits}f}"
    return str(value)


def _write_csv(path: Path, fieldnames: Sequence[str], rows: Sequence[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames, lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({k: row.get(k, "") for k in fieldnames})


def load_raw_runs(raw_dir: Path, suites_dir: Path | None = None) -> dict[str, list[dict[str, Any]]]:
    """Load results/comparison/raw/<method>/run-XX.json grouped by method, sorted by run."""
    by_method: dict[str, list[dict[str, Any]]] = {}
    if not raw_dir.is_dir():
        return by_method
    if suites_dir is None and raw_dir.parent.name == "comparison" and raw_dir.parent.parent.name == "results":
        suites_dir = raw_dir.parent.parent.parent / "test-suites"
    for json_path in sorted(raw_dir.glob("*/run-*.json")):
        data = json.loads(json_path.read_text(encoding="utf-8"))
        method = str(data["method"])
        if method in VARIANT_PARENTS and "iterations" not in data:
            # measure.py saves the final metrics; the generator owns the iteration trace.
            label = f"run-{data['run']:02d}"
            trace = suites_dir / method / label / "generation.json" if suites_dir is not None else None
            if trace is None or not trace.is_file():
                print(f"Missing iteration history for {method} {label}. Supply --suites-dir.", file=sys.stderr)
            else:
                generation = json.loads(trace.read_text(encoding="utf-8"))
                if (generation["method"] != method or generation["run"] != data["run"]
                        or generation["generation_s"] != data["time"]["generation_s"]):
                    raise ValueError(f"{trace}: generation does not match {json_path}")
                data["iterations"] = generation["iterations"]
                data["baseline"] = generation["baseline"]
        by_method.setdefault(method, []).append(data)
    for records in by_method.values():
        records.sort(key=lambda item: int(item["run"]))
    return by_method


def _backfill_alive(rec: dict[str, Any], mutation_dir: Path) -> tuple[list[dict[str, Any]] | None, str]:
    """Rebuild mutation.alive in memory from saved PIT/custom reports of a run measured before T17."""
    run_dir = mutation_dir / str(rec["method"]) / f"run-{int(rec['run']):02d}"
    if not (run_dir / "mutations.xml").is_file():
        return None, f"no alive list and no `{run_dir.name}/mutations.xml`"
    pit, custom = read_suite_results(run_dir)
    measured = merge_mutations(pit, custom)
    stored = rec["mutation"]
    if measured["total"] != stored["total"] or (
        "status_counts" in stored and measured["status_counts"] != stored["status_counts"]
    ):
        return None, "saved reports do not match stored counts"
    return alive_mutants(pit, custom), "backfilled from saved reports"


def apply_equivalents(
    by_method: dict[str, list[dict[str, Any]]],
    mutation_dir: Path,
    equivalents: Path,
) -> list[dict[str, Any]]:
    """Recompute every mutation score with the current equivalent-mutants.csv, in memory.

    Runs whose JSON lacks mutation.alive (measured before T17) are backfilled from
    results/mutation/<method>/run-XX/mutations.xml and custom-mutations.json; the raw
    JSON files are not rewritten. Runs that cannot be rescored keep their stored score.
    """
    rows = load_equivalents(equivalents) if equivalents.is_file() else []
    report: list[dict[str, Any]] = []
    for method in ordered_methods(by_method):
        for rec in by_method.get(method, []):
            stored = rec.get("mutation")
            if not isinstance(stored, dict):
                continue
            if "alive" in stored:
                alive, origin = stored["alive"], "stored alive list"
            else:
                alive, origin = _backfill_alive(rec, mutation_dir)
            entry = {
                "method": method,
                "run": int(rec["run"]),
                "total": stored["total"],
                "stored_equivalent": stored["equivalent"],
                "stored_score": stored["score"],
                "origin": origin,
                "rescored": alive is not None,
            }
            if alive is not None:
                rec["mutation"] = rescore({**stored, "alive": alive}, rows)
            entry["equivalent"] = rec["mutation"]["equivalent"]
            entry["score"] = rec["mutation"]["score"]
            report.append(entry)
    return report


def _equivalence_md(report: Sequence[dict[str, Any]], equivalents: Path, csv_rows: int) -> list[str]:
    try:
        shown = equivalents.resolve().relative_to(ROOT).as_posix()
    except ValueError:
        shown = equivalents.as_posix()
    lines = [
        "",
        "## Equivalent mutants excluded",
        "",
        "Mutation scores in this report are recomputed from the undetected mutants of each run with the current "
        f"`{shown}` ({csv_rows} rows): `score = killed / (total - equivalent) * 100`. "
        "Runs that cannot be recomputed keep the score stored in their JSON.",
        "",
    ]
    if not report:
        lines.append("No runs with mutation data (`n = 0`).")
        return lines
    rescored = [row for row in report if row["rescored"]]
    lines.append(
        f"Equivalent mutants excluded in total: {sum(row['equivalent'] for row in rescored)} "
        f"across {len(rescored)} recomputed runs (of {len(report)} runs with mutation data)."
    )
    lines.extend(
        [
            "",
            "| Method | Run | Total | Equivalent excluded | Stored equivalent | Stored score (%) | Score used (%) | Source |",
            "|---|---|---:|---:|---:|---:|---:|---|",
        ]
    )
    for row in report:
        excluded = row["equivalent"] if row["rescored"] else "not recomputed"
        lines.append(
            f"| `{row['method']}` | `run-{row['run']:02d}` | {row['total']} | {excluded} "
            f"| {row['stored_equivalent']} | {_fmt_md(float(row['stored_score']))} "
            f"| {_fmt_md(float(row['score']))} | {row['origin']} |"
        )
    return lines


def ordered_methods(by_method: dict[str, list[dict[str, Any]]]) -> list[str]:
    variants = [m for chain in VARIANT_CHAINS.values() for m in chain if m in by_method]
    extras = sorted(by_method.keys() - set(CANONICAL_METHODS) - set(variants))
    return [*CANONICAL_METHODS, *variants, *extras]


def _extract_metric_series(
    records: Sequence[dict[str, Any]],
    extractor: Callable[[dict[str, Any]], float | int | None],
) -> list[float | int | None]:
    return [extractor(rec) for rec in records]


def write_summary(
    by_method: dict[str, list[dict[str, Any]]],
    out_dir: Path,
    equivalence: Sequence[str] = (),
) -> list[dict[str, Any]]:
    methods = ordered_methods(by_method)
    csv_rows: list[dict[str, Any]] = []
    md_lines = [
        "# Experiment Summary Statistics",
        "",
        "Descriptive statistics computed strictly from `results/comparison/raw/**/run-*.json`. "
        "Missing data are reported as `n = 0`. Mutation scores are recomputed with the current "
        "equivalent-mutant list (see the last section).",
        "",
        "| Method | Metric | Unit | n | Mean | Std (ddof=1) | Median | IQR | Min | Max |",
        "|---|---|---|---:|---:|---:|---:|---:|---:|---:|",
    ]
    for method in methods:
        records = by_method.get(method, [])
        for key, label, unit, extractor in METRICS:
            stats = describe(_extract_metric_series(records, extractor))
            csv_rows.append(
                {
                    "method": method,
                    "metric": key,
                    "unit": unit,
                    "n": stats.n,
                    "mean": _fmt_csv(stats.mean),
                    "std": _fmt_csv(stats.std),
                    "median": _fmt_csv(stats.median),
                    "iqr": _fmt_csv(stats.iqr),
                    "min": _fmt_csv(stats.min),
                    "max": _fmt_csv(stats.max),
                }
            )
            md_lines.append(
                f"| `{method}` | {label} (`{key}`) | {unit} | {stats.n} "
                f"| {_fmt_md(stats.mean)} | {_fmt_md(stats.std)} | {_fmt_md(stats.median)} "
                f"| {_fmt_md(stats.iqr)} | {_fmt_md(stats.min)} | {_fmt_md(stats.max)} |"
            )

    _write_csv(
        out_dir / "summary.csv",
        ["method", "metric", "unit", "n", "mean", "std", "median", "iqr", "min", "max"],
        csv_rows,
    )
    md_lines.extend(equivalence)
    (out_dir / "summary.md").write_text("\n".join(md_lines) + "\n", encoding="utf-8", newline="\n")
    return csv_rows


def write_pairwise(
    by_method: dict[str, list[dict[str, Any]]],
    out_dir: Path,
) -> list[dict[str, Any]]:
    csv_rows: list[dict[str, Any]] = []
    md_lines = [
        "# Pairwise Statistical Comparisons",
        "",
        "Two-sided Mann-Whitney U test and Vargha-Delaney A12 effect size "
        "(`A12 = P(A > B) + 0.5 * P(A == B)`; magnitude thresholds: 0.56 small, 0.64 medium, 0.71 large). "
        "Methods with `n < 2` (including single-run `p1-manual`) are excluded from inferential tests.",
        "",
        "| Pair (A vs B) | RQ | Metric | n_A | n_B | Median A | Median B | Mann-Whitney U | p-value | A12 | Magnitude |",
        "|---|---|---|---:|---:|---:|---:|---:|---:|---:|---|",
    ]
    comparisons = PAIRWISE_COMPARISONS
    if any(method in by_method for method in REDUCED_CHAIN):
        comparisons += REDUCED_COMPARISONS
    if any(method in by_method for method in WEAK_CHAIN):
        comparisons += WEAK_COMPARISONS
    for method_a, method_b, rq in comparisons:
        pair_label = f"{method_a} vs {method_b}"
        recs_a = by_method.get(method_a, [])
        recs_b = by_method.get(method_b, [])
        for key, _label, _unit, extractor in METRICS:
            res = compare_samples(
                _extract_metric_series(recs_a, extractor),
                _extract_metric_series(recs_b, extractor),
                min_n=2,
            )
            csv_rows.append(
                {
                    "pair": pair_label,
                    "method_a": method_a,
                    "method_b": method_b,
                    "rq": rq,
                    "metric": key,
                    "n_a": res.n_a,
                    "n_b": res.n_b,
                    "mean_a": _fmt_csv(res.mean_a),
                    "mean_b": _fmt_csv(res.mean_b),
                    "median_a": _fmt_csv(res.median_a),
                    "median_b": _fmt_csv(res.median_b),
                    "u_statistic": _fmt_csv(res.u_statistic),
                    "p_value": _fmt_csv(res.p_value),
                    "a12": _fmt_csv(res.a12),
                    "effect_size": res.effect_size or "",
                }
            )
            md_lines.append(
                f"| `{method_a}` vs `{method_b}` | {rq} | `{key}` | {res.n_a} | {res.n_b} "
                f"| {_fmt_md(res.median_a)} | {_fmt_md(res.median_b)} "
                f"| {_fmt_md(res.u_statistic)} | {_fmt_md(res.p_value)} "
                f"| {_fmt_md(res.a12)} | {res.effect_size or '—'} |"
            )

    _write_csv(
        out_dir / "pairwise.csv",
        [
            "pair",
            "method_a",
            "method_b",
            "rq",
            "metric",
            "n_a",
            "n_b",
            "mean_a",
            "mean_b",
            "median_a",
            "median_b",
            "u_statistic",
            "p_value",
            "a12",
            "effect_size",
        ],
        csv_rows,
    )
    (out_dir / "pairwise.md").write_text("\n".join(md_lines) + "\n", encoding="utf-8", newline="\n")
    return csv_rows


def _diff(after: float | None, before: float | None) -> float | None:
    if after is None or before is None:
        return None
    return float(after) - float(before)


def write_coverage_before_after(
    by_method: dict[str, list[dict[str, Any]]],
    out_dir: Path,
    *,
    reduced: bool = False,
    variant: str | None = None,
) -> list[dict[str, Any]]:
    variant = variant or ("reduced" if reduced else "full")
    chain = CANONICAL_METHODS[2:] if variant == "full" else VARIANT_CHAINS[variant]
    p3, p4, p4b = (method.split("-", 1)[0] for method in chain)
    p3_map, p4_map, p4b_map = ({int(r["run"]): r for r in by_method.get(method, [])} for method in chain)
    filename = "coverage-before-after" if variant == "full" else f"coverage-before-after-{variant}"
    columns = [
        "run", f"{p3}_statement_pct", f"{p4}_statement_pct", f"delta_{p4}_{p3}_statement_pct",
        f"{p3}_branch_pct", f"{p4}_branch_pct", f"delta_{p4}_{p3}_branch_pct",
        f"{p3}_function_pct", f"{p4}_function_pct", f"delta_{p4}_{p3}_function_pct",
        f"{p3}_mutation_score", f"{p4}_mutation_score", f"{p4b}_mutation_score", f"delta_{p4b}_{p4}_mutation_score",
    ]
    all_runs = sorted(set(p3_map) | set(p4_map) | set(p4b_map))

    csv_rows: list[dict[str, Any]] = []
    md_lines = [
        f"# Paired Before/After Comparison ({p3.upper()} → {p4.upper()} Coverage, {p4.upper()} → {p4b.upper()} Mutation)",
        "",
    ]
    if not all_runs:
        md_lines.append("No paired runs available (`n = 0`).")
    else:
        md_lines.extend(
            [
                f"| Run | {p3.upper()} Stmt (%) | {p4.upper()} Stmt (%) | Δ Stmt (pp) | {p3.upper()} Branch (%) | {p4.upper()} Branch (%) | Δ Branch (pp) | {p3.upper()} Func (%) | {p4.upper()} Func (%) | Δ Func (pp) | {p4.upper()} Mut (%) | {p4b.upper()} Mut (%) | Δ Mut (pp) |",
                "|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|",
            ]
        )

    for run in all_runs:
        r3 = p3_map.get(run)
        r4 = p4_map.get(run)
        r4b = p4b_map.get(run)

        p3_stmt = r3["coverage"]["statement"]["pct"] if r3 else None
        p4_stmt = r4["coverage"]["statement"]["pct"] if r4 else None
        p3_branch = r3["coverage"]["branch"]["pct"] if r3 else None
        p4_branch = r4["coverage"]["branch"]["pct"] if r4 else None
        p3_func = r3["coverage"]["function"]["pct"] if r3 else None
        p4_func = r4["coverage"]["function"]["pct"] if r4 else None
        p3_mut = _get_mutation_score(r3) if r3 else None
        p4_mut = _get_mutation_score(r4) if r4 else None
        p4b_mut = _get_mutation_score(r4b) if r4b else None

        d_stmt = _diff(p4_stmt, p3_stmt)
        d_branch = _diff(p4_branch, p3_branch)
        d_func = _diff(p4_func, p3_func)
        d_mut = _diff(p4b_mut, p4_mut)

        run_label = f"run-{run:02d}"
        values = (p3_stmt, p4_stmt, d_stmt, p3_branch, p4_branch, d_branch, p3_func, p4_func, d_func,
                  p3_mut, p4_mut, p4b_mut, d_mut)
        csv_rows.append(dict(zip(columns, [run_label, *map(_fmt_csv, values)], strict=True)))
        md_lines.append(
            f"| `{run_label}` | {_fmt_md(p3_stmt, 2)} | {_fmt_md(p4_stmt, 2)} | {_fmt_md(d_stmt, 2)} "
            f"| {_fmt_md(p3_branch, 2)} | {_fmt_md(p4_branch, 2)} | {_fmt_md(d_branch, 2)} "
            f"| {_fmt_md(p3_func, 2)} | {_fmt_md(p4_func, 2)} | {_fmt_md(d_func, 2)} "
            f"| {_fmt_md(p4_mut, 2)} | {_fmt_md(p4b_mut, 2)} | {_fmt_md(d_mut, 2)} |"
        )

    _write_csv(
        out_dir / f"{filename}.csv",
        columns,
        csv_rows,
    )
    (out_dir / f"{filename}.md").write_text("\n".join(md_lines) + "\n", encoding="utf-8", newline="\n")
    return csv_rows


def write_iterations(
    by_method: dict[str, list[dict[str, Any]]],
    out_dir: Path,
) -> list[dict[str, Any]]:
    csv_rows: list[dict[str, Any]] = []
    for method in ("p4-coverage-guided", "p4b-mutation-feedback", *VARIANT_PARENTS):
        for rec in by_method.get(method, []):
            run = int(rec["run"])
            for item in rec.get("iterations") or []:
                csv_rows.append(
                    {
                        "method": method,
                        "run": f"run-{run:02d}",
                        "iteration": int(item["i"]),
                        "statement_pct": _fmt_csv(item.get("statement_pct")),
                        "branch_pct": _fmt_csv(item.get("branch_pct")),
                        "kept_tests": _fmt_csv(item.get("kept_tests")),
                        "tokens": _fmt_csv(item.get("tokens")),
                        "mutation_score": _fmt_csv(item.get("mutation_score")),
                    }
                )
    _write_csv(
        out_dir / "iterations.csv",
        ["method", "run", "iteration", "statement_pct", "branch_pct", "kept_tests", "tokens", "mutation_score"],
        csv_rows,
    )
    return csv_rows


def write_rq5_correlation(
    by_method: dict[str, list[dict[str, Any]]],
    out_dir: Path,
) -> list[dict[str, Any]]:
    all_records: list[dict[str, Any]] = []
    for method in ordered_methods(by_method):
        all_records.extend(by_method.get(method, []))

    extractors = {key: fn for key, _label, _unit, fn in METRICS}
    csv_rows: list[dict[str, Any]] = []
    for var_x, var_y in RQ5_CORRELATION_PAIRS:
        xs = _extract_metric_series(all_records, extractors[var_x])
        ys = _extract_metric_series(all_records, extractors[var_y])
        corr = spearman_correlation(xs, ys)
        csv_rows.append(
            {
                "var_x": var_x,
                "var_y": var_y,
                "n": corr.n,
                "spearman_rho": _fmt_csv(corr.rho),
                "p_value": _fmt_csv(corr.p_value),
            }
        )
    _write_csv(
        out_dir / "rq5-correlation.csv",
        ["var_x", "var_y", "n", "spearman_rho", "p_value"],
        csv_rows,
    )
    return csv_rows


def _plot_coverage_boxplot(by_method: dict[str, list[dict[str, Any]]], fig_dir: Path) -> Path:
    path = fig_dir / "coverage_boxplot.png"
    methods = [m for m in ordered_methods(by_method) if by_method.get(m)]
    fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
    panels = [
        (axes[0], "statement", "Statement / Line Coverage (%)"),
        (axes[1], "branch", "Branch Coverage (%)"),
    ]
    for ax, key, subtitle in panels:
        series = []
        labels = []
        for method in methods:
            vals = [float(r["coverage"][key]["pct"]) for r in by_method[method]]
            if vals:
                series.append(vals)
                labels.append(f"{METHOD_LABELS.get(method, method)}\n(n={len(vals)})")
        if series:
            ax.boxplot(series, tick_labels=labels, patch_artist=True, boxprops={"facecolor": "#d6e6f5"})
            for idx, vals in enumerate(series, start=1):
                ax.scatter([idx] * len(vals), vals, color="#1f4e79", s=22, zorder=3, alpha=0.8)
        else:
            ax.text(0.5, 0.5, "No data (n = 0)", ha="center", va="center", transform=ax.transAxes)
        ax.set_title(subtitle)
        ax.set_xlabel("Method")
        ax.set_ylabel("Coverage (%)")
        ax.set_ylim(0, 105)
        ax.grid(axis="y", linestyle="--", alpha=0.4)
        ax.tick_params(axis="x", rotation=15)

    fig.suptitle("Code Coverage Distribution by Method")
    fig.tight_layout()
    fig.savefig(path, dpi=150)
    plt.close(fig)
    return path


def _plot_mutation_boxplot(by_method: dict[str, list[dict[str, Any]]], fig_dir: Path) -> Path:
    path = fig_dir / "mutation_boxplot.png"
    methods = ordered_methods(by_method)
    series: list[list[float]] = []
    labels: list[str] = []
    for method in methods:
        vals = [float(score) for r in by_method.get(method, []) if (score := _get_mutation_score(r)) is not None]
        if vals:
            series.append(vals)
            labels.append(f"{METHOD_LABELS.get(method, method)}\n(n={len(vals)})")

    fig, ax = plt.subplots(figsize=(8, 5))
    if series:
        ax.boxplot(series, tick_labels=labels, patch_artist=True, boxprops={"facecolor": "#fde2c4"})
        for idx, vals in enumerate(series, start=1):
            ax.scatter([idx] * len(vals), vals, color="#a65200", s=24, zorder=3, alpha=0.85)
    else:
        ax.text(0.5, 0.5, "No mutation data (n = 0)", ha="center", va="center", transform=ax.transAxes)
    ax.set_title("Mutation Score Distribution by Method")
    ax.set_xlabel("Method")
    ax.set_ylabel("Mutation Score (%)")
    ax.set_ylim(0, 105)
    ax.grid(axis="y", linestyle="--", alpha=0.4)
    ax.tick_params(axis="x", rotation=15)
    fig.tight_layout()
    fig.savefig(path, dpi=150)
    plt.close(fig)
    return path


def _plot_p4_iterations(by_method: dict[str, list[dict[str, Any]]], fig_dir: Path,
                        method: str = "p4-coverage-guided") -> Path:
    code = method.split("-", 1)[0]
    mutation = method.endswith("-mutation-feedback")
    metric = "Mutation Score" if mutation else "Coverage"
    suffix = "mutation" if mutation else "coverage"
    path = fig_dir / f"{code}_iterations_{suffix}.png"
    fig, ax = plt.subplots(figsize=(8, 5))
    series = [("mutation_score", "Mutation Score", "#9467bd", "-")] if mutation else [
        ("branch_pct", "Branch Coverage", "#1f77b4", "-"),
        ("statement_pct", "Statement/Line Coverage", "#2ca02c", "--"),
    ]
    records = by_method.get(method, [])
    parents = {r["run"]: r for r in by_method.get(VARIANT_PARENTS.get(method), [])}
    ticks = set()
    for key, label, color, style in series:
        by_iteration: dict[int, list[float]] = {}
        for rec in records:
            iterations = rec.get("iterations")
            if iterations is None:
                continue
            points = {int(item["i"]): float(item[key]) for item in iterations if item.get(key) is not None}
            # Generation records its first feedback call as iteration 1.
            seed = parents.get(rec["run"])
            if key in rec.get("baseline", {}) and 0 not in points:
                points[0] = rec["baseline"][key]
            elif seed and 0 not in points:
                baseline = (_get_mutation_score(seed) if mutation
                            else seed["coverage"][key.removesuffix("_pct")]["pct"])
                if baseline is not None:
                    points[0] = baseline
            xs = sorted(points)
            ax.plot(xs, [points[x] for x in xs], color=color, linestyle=style,
                    marker=".", alpha=0.25, linewidth=1.2)
            for x, y in points.items():
                by_iteration.setdefault(x, []).append(y)
        if by_iteration:
            xs = sorted(by_iteration)
            ticks.update(xs)
            ax.plot(xs, [np.mean(by_iteration[x]) for x in xs], color=color, linewidth=2.4,
                    linestyle=style, marker="o", label=f"Mean {label} (%)")
    if ticks:
        ax.set_xticks(sorted(ticks))
        ax.legend(loc="lower right")
    else:
        ax.text(0.5, 0.5, "No iteration data", ha="center", va="center", transform=ax.transAxes)
    ax.set_title(f"{METHOD_LABELS[method]} Progression Across Iterations")
    ax.set_xlabel("Iteration")
    ax.set_ylabel(f"{metric} (%)")
    ax.set_ylim(0, 105)
    ax.grid(True, linestyle="--", alpha=0.4)
    fig.tight_layout()
    fig.savefig(path, dpi=150)
    plt.close(fig)
    return path


def _plot_tests_vs_mutation(by_method: dict[str, list[dict[str, Any]]], fig_dir: Path) -> Path:
    path = fig_dir / "tests_vs_mutation_scatter.png"
    fig, ax = plt.subplots(figsize=(8, 5))
    colors = {
        "p1-manual": "#111111",
        "p2-random": "#7f7f7f",
        "p3-direct-llm": "#d62728",
        "p4-coverage-guided": "#1f77b4",
        "p4b-mutation-feedback": "#2ca02c",
        "evosuite": "#9467bd",
    }
    markers = {
        "p1-manual": "D",
        "p2-random": "x",
        "p3-direct-llm": "^",
        "p4-coverage-guided": "o",
        "p4b-mutation-feedback": "s",
        "evosuite": "P",
    }

    plotted = False
    for method in ordered_methods(by_method):
        xs: list[float] = []
        ys: list[float] = []
        for rec in by_method.get(method, []):
            score = _get_mutation_score(rec)
            if score is None:
                continue
            xs.append(float(rec["tests"]["valid"]))
            ys.append(float(score))
        if xs:
            plotted = True
            ax.scatter(
                xs,
                ys,
                label=f"{METHOD_LABELS.get(method, method)} (n={len(xs)})",
                color=colors.get(method),
                marker=markers.get(method, "o"),
                s=45,
                alpha=0.85,
            )

    if plotted:
        ax.legend(loc="lower right")
    else:
        ax.text(0.5, 0.5, "No paired test/mutation data (n = 0)", ha="center", va="center", transform=ax.transAxes)

    ax.set_title("Valid Test Count vs. Mutation Score")
    ax.set_xlabel("Valid Test Count (tests)")
    ax.set_ylabel("Mutation Score (%)")
    ax.set_ylim(0, 105)
    ax.grid(True, linestyle="--", alpha=0.4)
    fig.tight_layout()
    fig.savefig(path, dpi=150)
    plt.close(fig)
    return path


def generate_figures(by_method: dict[str, list[dict[str, Any]]], out_dir: Path) -> list[Path]:
    fig_dir = out_dir / "figures"
    fig_dir.mkdir(parents=True, exist_ok=True)
    return [
        _plot_coverage_boxplot(by_method, fig_dir),
        _plot_mutation_boxplot(by_method, fig_dir),
        _plot_p4_iterations(by_method, fig_dir),
        *(_plot_p4_iterations(by_method, fig_dir, method) for method in VARIANT_PARENTS if method in by_method),
        _plot_tests_vs_mutation(by_method, fig_dir),
    ]


def analyze(
    *,
    raw_dir: Path,
    out_dir: Path,
    mutation_dir: Path | None = None,
    equivalents: Path | None = None,
    suites_dir: Path | None = None,
) -> dict[str, Path]:
    """Run full statistical analysis and figure generation from raw_dir into out_dir.

    mutation_dir defaults to results/mutation beside results/comparison/raw, and
    equivalents to mutation_dir/equivalent-mutants.csv.
    """
    out_dir.mkdir(parents=True, exist_ok=True)
    mutation_dir = mutation_dir if mutation_dir is not None else raw_dir.parent.parent / "mutation"
    equivalents = equivalents if equivalents is not None else mutation_dir / "equivalent-mutants.csv"
    by_method = load_raw_runs(raw_dir, suites_dir)
    report = apply_equivalents(by_method, mutation_dir, equivalents)
    csv_rows = len(load_equivalents(equivalents)) if equivalents.is_file() else 0
    write_summary(by_method, out_dir, _equivalence_md(report, equivalents, csv_rows))
    write_pairwise(by_method, out_dir)
    write_coverage_before_after(by_method, out_dir)
    write_iterations(by_method, out_dir)
    write_rq5_correlation(by_method, out_dir)
    figures = generate_figures(by_method, out_dir)
    outputs = {
        "summary_csv": out_dir / "summary.csv",
        "summary_md": out_dir / "summary.md",
        "pairwise_csv": out_dir / "pairwise.csv",
        "pairwise_md": out_dir / "pairwise.md",
        "before_after_csv": out_dir / "coverage-before-after.csv",
        "before_after_md": out_dir / "coverage-before-after.md",
        "iterations_csv": out_dir / "iterations.csv",
        "rq5_correlation_csv": out_dir / "rq5-correlation.csv",
    }
    for variant, chain in VARIANT_CHAINS.items():
        if any(method in by_method for method in chain):
            write_coverage_before_after(by_method, out_dir, variant=variant)
            key = variant.replace("-", "_")
            for ext in ("csv", "md"):
                outputs[f"{key}_before_after_{ext}"] = out_dir / f"coverage-before-after-{variant}.{ext}"
    for fig_path in figures:
        outputs[fig_path.stem] = fig_path
    return outputs


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Analyze experiment raw JSON outputs and generate tables/figures.")
    parser.add_argument("--suites-dir", type=Path, help="Test suites containing variant iteration histories "
                        "(default: test-suites beside results/ for a standard raw directory)")
    parser.add_argument(
        "--raw-dir",
        type=Path,
        default=ROOT / "results/comparison/raw",
        help="Directory containing <method>/run-XX.json files",
    )
    parser.add_argument(
        "--out-dir",
        type=Path,
        default=ROOT / "results/comparison",
        help="Output directory for CSV, Markdown, and figures/",
    )
    parser.add_argument(
        "--mutation-dir",
        type=Path,
        default=None,
        help="Saved mutation reports <method>/run-XX/ used to backfill runs without mutation.alive "
        "(default: the mutation directory beside the comparison directory of --raw-dir)",
    )
    parser.add_argument(
        "--equivalents",
        type=Path,
        default=None,
        help="Equivalent mutant CSV (default: <mutation-dir>/equivalent-mutants.csv)",
    )
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    outputs = analyze(
        raw_dir=args.raw_dir,
        out_dir=args.out_dir,
        mutation_dir=args.mutation_dir,
        equivalents=args.equivalents,
        suites_dir=args.suites_dir,
    )
    for name, path in outputs.items():
        print(f"{name}: {path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
