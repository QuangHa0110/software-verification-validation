"""Run full or partial experiment workflows across P1-P4b with paired ordering and resumability."""

from __future__ import annotations

import argparse
from collections.abc import Callable, Sequence
from dataclasses import dataclass
from datetime import datetime, timezone
import json
from pathlib import Path
import re
import subprocess
import sys
from time import perf_counter

ROOT = Path(__file__).resolve().parents[2]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from tools.svv.config import load_config
from tools.svv.llm_client import LlmQuotaExceeded, _QUOTA

QUOTA_EXIT_CODE = 3

DEFAULT_METHODS = (
    "p1-manual",
    "p2-random",
    "p3-direct-llm",
    "p4-coverage-guided",
    "p4b-mutation-feedback",
)
KNOWN_METHODS = (*DEFAULT_METHODS, "evosuite")
PAIRED_LLM_METHODS = (
    "p3-direct-llm",
    "p4-coverage-guided",
    "p4b-mutation-feedback",
)
LLM_QUOTA_RETURN_CODES = (2, 3)

_QUOTA_EXTRA = re.compile(r"LlmQuotaExceeded|quota\s+exceeded", re.I)


@dataclass(frozen=True)
class Step:
    method: str
    run: int
    kind: str  # "generate" | "measure" | "skip"
    command: tuple[str, ...]
    reason: str = ""
    script_path: Path | None = None


def parse_methods(raw: str | None) -> list[str]:
    if raw is None or not raw.strip():
        return list(DEFAULT_METHODS)
    methods: list[str] = []
    for part in raw.split(","):
        name = part.strip()
        if not name:
            continue
        if name not in KNOWN_METHODS:
            raise ValueError(f"Unknown method '{name}'. Valid methods: {', '.join(KNOWN_METHODS)}")
        if name not in methods:
            methods.append(name)
    if not methods:
        raise ValueError("At least one method must be specified")
    return methods


def parse_runs(raw: str | None, default_runs: int = 10) -> list[int]:
    if raw is None or not raw.strip():
        if default_runs < 1:
            raise ValueError("default_runs must be >= 1")
        return list(range(1, default_runs + 1))
    selected: set[int] = set()
    for token in raw.split(","):
        item = token.strip()
        if not item:
            continue
        if "-" in item:
            bounds = item.split("-")
            if len(bounds) != 2 or not bounds[0].isdigit() or not bounds[1].isdigit():
                raise ValueError(f"Invalid run range '{item}'")
            start, end = int(bounds[0]), int(bounds[1])
            if start < 1 or end < start:
                raise ValueError(f"Invalid run range '{item}': expected 1 <= start <= end")
            selected.update(range(start, end + 1))
        else:
            if not item.isdigit():
                raise ValueError(f"Invalid run number '{item}'")
            val = int(item)
            if val < 1:
                raise ValueError(f"Run number must be >= 1, got {val}")
            selected.add(val)
    if not selected:
        raise ValueError("No valid runs specified")
    return sorted(selected)


def _llm_override_args(provider: str | None, model: str | None, effort: str | None) -> list[str]:
    extra: list[str] = []
    if provider:
        extra.extend(["--provider", provider])
    if model:
        extra.extend(["--model", model])
    if effort:
        extra.extend(["--effort", effort])
    return extra


def _generator_spec(
    method: str,
    run: int,
    *,
    root: Path,
    python_exe: str,
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
) -> tuple[Path | None, tuple[str, ...], str]:
    """Return (script_path, command, unavailable_reason) for a method's generator."""
    if method == "p1-manual":
        return None, (), ""
    if method == "p2-random":
        script = root / "tools/generate_p2.py"
        return script, (python_exe, str(script), "--run", str(run)), ""
    if method == "p3-direct-llm":
        script = root / "tools/generate_p3.py"
        cmd = (python_exe, str(script), "--run", str(run), *_llm_override_args(provider, model, effort))
        return script, cmd, ""
    if method == "p4-coverage-guided":
        script = root / "tools/generate_p4.py"
        cmd = (python_exe, str(script), "--run", str(run), *_llm_override_args(provider, model, effort))
        return script, cmd, ""
    if method == "p4b-mutation-feedback":
        script = root / "tools/generate_p4.py"
        cmd = (
            python_exe,
            str(script),
            "--run",
            str(run),
            "--mutation-feedback",
            *_llm_override_args(provider, model, effort),
        )
        return script, cmd, ""
    if method == "evosuite":
        return (
            None,
            (),
            "No EvoSuite generator tool configured (D-013/D-014: EvoSuite excluded in automated environment)",
        )
    raise ValueError(f"Unsupported method: {method}")


def _measure_command(
    method: str,
    run: int,
    *,
    root: Path,
    python_exe: str,
    with_mutation: bool,
) -> tuple[str, ...]:
    script = root / "experiments/scripts/measure.py"
    suite = root / "test-suites" / method / f"run-{run:02d}"
    cmd = [python_exe, str(script), "--suite", str(suite), "--method", method, "--run", str(run)]
    if with_mutation:
        cmd.append("--mutation")
    return tuple(cmd)


def build_plan(
    methods: Sequence[str],
    runs: Sequence[int],
    *,
    root: Path = ROOT,
    python_exe: str = sys.executable,
    skip_generation: bool = False,
    with_mutation: bool = True,
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
) -> list[Step]:
    """Build deterministic execution plan respecting P3 -> P4 -> P4b paired ordering per run."""
    plan: list[Step] = []
    selected = set(methods)

    # 1. P1 manual: run-01 only, no generation step.
    if "p1-manual" in selected:
        if 1 in runs:
            plan.append(
                Step(
                    method="p1-manual",
                    run=1,
                    kind="measure",
                    command=_measure_command(
                        "p1-manual", 1, root=root, python_exe=python_exe, with_mutation=with_mutation
                    ),
                )
            )
        else:
            plan.append(
                Step(
                    method="p1-manual",
                    run=1,
                    kind="skip",
                    command=(),
                    reason="p1-manual only has run-01, which is not in requested --runs",
                )
            )

    # 2. P2 random: independent per run.
    if "p2-random" in selected:
        for run in runs:
            if not skip_generation:
                script, cmd, _ = _generator_spec("p2-random", run, root=root, python_exe=python_exe)
                plan.append(Step(method="p2-random", run=run, kind="generate", command=cmd, script_path=script))
            plan.append(
                Step(
                    method="p2-random",
                    run=run,
                    kind="measure",
                    command=_measure_command(
                        "p2-random", run, root=root, python_exe=python_exe, with_mutation=with_mutation
                    ),
                )
            )

    # 3. Paired LLM methods: strictly P3 run-XX -> P4 run-XX -> P4b run-XX for each run.
    paired_selected = [m for m in PAIRED_LLM_METHODS if m in selected]
    if paired_selected:
        for run in runs:
            for method in paired_selected:
                if not skip_generation:
                    script, cmd, _ = _generator_spec(
                        method,
                        run,
                        root=root,
                        python_exe=python_exe,
                        provider=provider,
                        model=model,
                        effort=effort,
                    )
                    plan.append(Step(method=method, run=run, kind="generate", command=cmd, script_path=script))
                plan.append(
                    Step(
                        method=method,
                        run=run,
                        kind="measure",
                        command=_measure_command(
                            method, run, root=root, python_exe=python_exe, with_mutation=with_mutation
                        ),
                    )
                )

    # 4. EvoSuite (or any method without a generator tool).
    if "evosuite" in selected:
        _, _, reason = _generator_spec("evosuite", runs[0] if runs else 1, root=root, python_exe=python_exe)
        plan.append(Step(method="evosuite", run=0, kind="skip", command=(), reason=reason))

    return plan


def _format_runs_arg(runs: Sequence[int]) -> str:
    if not runs:
        return "1"
    sorted_runs = sorted(set(runs))
    ranges: list[str] = []
    start = prev = sorted_runs[0]
    for r in sorted_runs[1:]:
        if r == prev + 1:
            prev = r
        else:
            ranges.append(f"{start}" if start == prev else f"{start}-{prev}")
            start = prev = r
    ranges.append(f"{start}" if start == prev else f"{start}-{prev}")
    return ",".join(ranges)


def build_resume_command(
    methods: Sequence[str],
    runs: Sequence[int],
    *,
    skip_generation: bool = False,
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
) -> str:
    parts = [
        "python",
        "experiments/scripts/run_experiment.py",
        "--methods",
        ",".join(methods),
        "--runs",
        _format_runs_arg(runs),
        "--skip-existing",
    ]
    if skip_generation:
        parts.append("--skip-generation")
    parts.extend(_llm_override_args(provider, model, effort))
    return " ".join(parts)


def _is_quota_failure(step: Step, returncode: int, stdout: str, stderr: str) -> bool:
    combined = f"{stdout}\n{stderr}"
    if _QUOTA_EXTRA.search(combined) or _QUOTA.search(combined):
        return True
    if step.kind == "generate" and step.method in PAIRED_LLM_METHODS and returncode in LLM_QUOTA_RETURN_CODES:
        # Do not misclassify argparse CLI usage errors (exit 2) as quota failures.
        if returncode == 2 and "usage:" in stderr.lower() and "error:" in stderr.lower():
            return False
        return True
    return False


def _suite_has_generation(root: Path, method: str, run: int) -> bool:
    return (root / "test-suites" / method / f"run-{run:02d}" / "generation.json").is_file()


def _raw_result_exists(root: Path, method: str, run: int) -> bool:
    return (root / "results/comparison/raw" / method / f"run-{run:02d}.json").is_file()


def _suite_has_java_tests(root: Path, method: str, run: int) -> bool:
    suite_dir = root / "test-suites" / method / f"run-{run:02d}"
    return suite_dir.is_dir() and any(suite_dir.glob("*.java"))


def _append_log(log_path: Path, entry: dict) -> None:
    log_path.parent.mkdir(parents=True, exist_ok=True)
    with log_path.open("a", encoding="utf-8", newline="\n") as handle:
        handle.write(json.dumps(entry, ensure_ascii=False) + "\n")


def _default_runner(command: Sequence[str], cwd: Path) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        list(command),
        cwd=cwd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        shell=False,
    )


def execute_plan(
    plan: Sequence[Step],
    *,
    methods: Sequence[str],
    runs: Sequence[int],
    root: Path = ROOT,
    skip_existing: bool = False,
    skip_generation: bool = False,
    dry_run: bool = False,
    provider: str | None = None,
    model: str | None = None,
    effort: str | None = None,
    runner: Callable[[Sequence[str], Path], subprocess.CompletedProcess[str]] = _default_runner,
) -> int:
    """Execute or print the experiment plan, logging progress to results/comparison/run-log.jsonl."""
    log_path = root / "results/comparison/run-log.jsonl"
    skipped_methods_missing_tool: set[str] = set()

    if dry_run:
        print(f"Experiment plan ({len(plan)} steps, dry-run):")
        for idx, step in enumerate(plan, start=1):
            run_label = f"run-{step.run:02d}" if step.run > 0 else "-"
            if step.kind == "skip":
                print(f"{idx:02d}. [SKIP] {step.method} {run_label}: {step.reason}")
                continue
            status_note = ""
            if skip_existing:
                if step.kind == "generate" and _suite_has_generation(root, step.method, step.run):
                    status_note = " [would skip: generation.json exists]"
                elif step.kind == "measure" and _raw_result_exists(root, step.method, step.run):
                    status_note = " [would skip: raw result exists]"
            if step.kind == "generate" and step.script_path is not None and not step.script_path.is_file():
                status_note += f" [tool not present yet: {step.script_path.relative_to(root).as_posix()}]"
            cmd_str = " ".join(step.command)
            print(f"{idx:02d}. [{step.kind.upper()}] {step.method} {run_label}{status_note} -> {cmd_str}")
        return 0

    for step in plan:
        run_label = f"run-{step.run:02d}" if step.run > 0 else "-"
        now_iso = datetime.now(timezone.utc).isoformat()

        if step.kind == "skip":
            print(f"[SKIP] {step.method}: {step.reason}", flush=True)
            _append_log(
                log_path,
                {
                    "timestamp": now_iso,
                    "method": step.method,
                    "run": step.run,
                    "step": "skip",
                    "status": "skipped",
                    "duration_s": 0.0,
                    "returncode": None,
                    "message": step.reason,
                },
            )
            continue

        if step.kind == "generate" and skip_existing and _suite_has_generation(root, step.method, step.run):
            msg = "generation.json already exists"
            print(f"[SKIP-EXISTING] {step.method} {run_label} generate ({msg})", flush=True)
            _append_log(
                log_path,
                {
                    "timestamp": now_iso,
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "skipped_existing",
                    "duration_s": 0.0,
                    "returncode": None,
                    "message": msg,
                },
            )
            continue

        if step.kind == "measure" and skip_existing and _raw_result_exists(root, step.method, step.run):
            msg = "raw result already exists"
            print(f"[SKIP-EXISTING] {step.method} {run_label} measure ({msg})", flush=True)
            _append_log(
                log_path,
                {
                    "timestamp": now_iso,
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "skipped_existing",
                    "duration_s": 0.0,
                    "returncode": None,
                    "message": msg,
                },
            )
            continue

        if step.kind == "generate" and step.script_path is not None and not step.script_path.is_file():
            rel = step.script_path.relative_to(root).as_posix()
            msg = f"Generator tool not found: {rel}"
            skipped_methods_missing_tool.add(step.method)
            print(f"[SKIP] {step.method} {run_label} generate: {msg}", flush=True)
            _append_log(
                log_path,
                {
                    "timestamp": now_iso,
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "skipped",
                    "duration_s": 0.0,
                    "returncode": None,
                    "message": msg,
                },
            )
            continue

        if (
            step.kind == "measure"
            and step.method in skipped_methods_missing_tool
            and not _suite_has_java_tests(root, step.method, step.run)
        ):
            msg = "Skipped measure because generator tool is unavailable and no test suite exists"
            print(f"[SKIP] {step.method} {run_label} measure: {msg}", flush=True)
            _append_log(
                log_path,
                {
                    "timestamp": now_iso,
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "skipped",
                    "duration_s": 0.0,
                    "returncode": None,
                    "message": msg,
                },
            )
            continue

        print(f"[RUN] {step.method} {run_label} {step.kind}", flush=True)
        started = perf_counter()
        try:
            proc = runner(step.command, root)
        except LlmQuotaExceeded as exc:
            elapsed = perf_counter() - started
            resume_cmd = build_resume_command(
                methods,
                runs,
                skip_generation=skip_generation,
                provider=provider,
                model=model,
                effort=effort,
            )
            _append_log(
                log_path,
                {
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "quota_exceeded",
                    "duration_s": round(elapsed, 4),
                    "returncode": QUOTA_EXIT_CODE,
                    "message": str(exc),
                },
            )
            print(
                f"LLM quota exceeded at {step.method} {run_label} ({step.kind}): {exc}\n"
                f"Resume with:\n  {resume_cmd}",
                file=sys.stderr,
                flush=True,
            )
            return QUOTA_EXIT_CODE

        elapsed = perf_counter() - started
        stdout = proc.stdout or ""
        stderr = proc.stderr or ""
        if stdout.strip():
            print(stdout.rstrip(), flush=True)

        if proc.returncode == 0:
            _append_log(
                log_path,
                {
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "ok",
                    "duration_s": round(elapsed, 4),
                    "returncode": 0,
                    "message": "",
                },
            )
            continue

        if _is_quota_failure(step, proc.returncode, stdout, stderr):
            resume_cmd = build_resume_command(
                methods,
                runs,
                skip_generation=skip_generation,
                provider=provider,
                model=model,
                effort=effort,
            )
            detail = stderr.strip() or stdout.strip() or f"exit {proc.returncode}"
            _append_log(
                log_path,
                {
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "method": step.method,
                    "run": step.run,
                    "step": step.kind,
                    "status": "quota_exceeded",
                    "duration_s": round(elapsed, 4),
                    "returncode": proc.returncode,
                    "message": detail[:500],
                },
            )
            print(
                f"LLM quota exceeded at {step.method} {run_label} ({step.kind}): {detail}\n"
                f"Resume with:\n  {resume_cmd}",
                file=sys.stderr,
                flush=True,
            )
            return QUOTA_EXIT_CODE

        detail = stderr.strip() or stdout.strip() or f"exit {proc.returncode}"
        _append_log(
            log_path,
            {
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "method": step.method,
                "run": step.run,
                "step": step.kind,
                "status": "failed",
                "duration_s": round(elapsed, 4),
                "returncode": proc.returncode,
                "message": detail[:500],
            },
        )
        print(
            f"Step failed at {step.method} {run_label} ({step.kind}), exit {proc.returncode}:\n{detail}",
            file=sys.stderr,
            flush=True,
        )
        return 1

    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Run experiment generation and measurement across methods and runs."
    )
    parser.add_argument(
        "--methods",
        default=None,
        help="Comma-separated method list (default: p1-manual,p2-random,p3-direct-llm,p4-coverage-guided,p4b-mutation-feedback)",
    )
    parser.add_argument(
        "--runs",
        default=None,
        help="Run numbers/ranges, e.g. '1-10' or '1,3,5' (default: 1..config.runs)",
    )
    parser.add_argument("--skip-generation", action="store_true", help="Skip test generation steps")
    parser.add_argument(
        "--skip-existing",
        action="store_true",
        help="Skip steps whose outputs (generation.json or raw run-XX.json) already exist",
    )
    parser.add_argument("--dry-run", action="store_true", help="Print planned steps without running them")
    parser.add_argument("--provider", default=None, help="Optional LLM provider override for P3/P4/P4b")
    parser.add_argument("--model", default=None, help="Optional LLM model override for P3/P4/P4b")
    parser.add_argument("--effort", default=None, help="Optional LLM effort override for P3/P4/P4b")
    return parser


def main(argv: Sequence[str] | None = None, *, root: Path = ROOT) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    try:
        config = load_config(root / "experiments/config")
        methods = parse_methods(args.methods)
        runs = parse_runs(args.runs, default_runs=int(config.get("runs", 10)))
    except (OSError, ValueError, KeyError) as exc:
        print(f"Configuration error: {exc}", file=sys.stderr)
        return 2

    with_mutation = bool(config.get("measure", {}).get("mutation", True))
    plan = build_plan(
        methods,
        runs,
        root=root,
        python_exe=sys.executable,
        skip_generation=args.skip_generation,
        with_mutation=with_mutation,
        provider=args.provider,
        model=args.model,
        effort=args.effort,
    )
    return execute_plan(
        plan,
        methods=methods,
        runs=runs,
        root=root,
        skip_existing=args.skip_existing,
        skip_generation=args.skip_generation,
        dry_run=args.dry_run,
        provider=args.provider,
        model=args.model,
        effort=args.effort,
    )


if __name__ == "__main__":
    sys.exit(main())
