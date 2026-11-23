"""Measure one suite in an isolated Maven project and publish a validated run."""

import argparse
import json
from pathlib import Path
import re
import shutil
import sys
import xml.etree.ElementTree as ET

import yaml
from jsonschema import Draft202012Validator, ValidationError

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from svv.config import load_config
from svv.jacoco import parse_jacoco
from svv.maven import run_maven
from svv.pit import parse_pit
from svv.surefire import parse_surefire
from svv.workdir import workdir


def read_generation(suite, method, run):
    path = suite / "generation.json"
    if not path.exists():
        return {}
    data = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(data, dict) or data.get("method", method) != method or data.get("run", run) != run:
        raise ValueError(f"{path}: expected metadata for {method} run {run}")
    return data


def llm_metrics(generation, config):
    supplied = generation.get("llm", {})
    calls = supplied.get("calls", 0)
    inputs = supplied.get("input_tokens", 0)
    outputs = supplied.get("output_tokens", 0)
    cost = 0.0
    if calls or inputs or outputs:
        price_in, price_out = config["price_per_mtok_input"], config["price_per_mtok_output"]
        cost = None if price_in is None or price_out is None else (inputs * price_in + outputs * price_out) / 1e6
    return {"calls": calls, "input_tokens": inputs, "output_tokens": outputs, "cost_usd": cost,
            "model": supplied.get("model", config["model"] if calls else ""),
            "effort": supplied.get("effort", config["effort"] if calls else "")}


def test_metrics(generation, report, passed, failed):
    # A generator may include pre-filter counts for tests discarded before Maven.
    supplied = generation.get("tests", {})
    generated = supplied.get("generated", report["tests"])
    compiled = supplied.get("compiled", report["tests"])
    result = {"generated": generated, "compiled": compiled, "valid": supplied.get("valid", passed),
              "passed": passed, "failed": failed, "skipped": report["skipped"],
              "dropped": supplied.get("dropped", []),
              "compile_rate": compiled / generated if generated else None}
    if "passed_reference" in supplied:
        result["passed_reference"] = supplied["passed_reference"]
        result["oracle_rate"] = supplied["passed_reference"] / compiled if compiled else None
    return result


def publish(work, results, method, label, result, coverage, mutation):
    """Publish JSON last. Replace previous reports, including stale optional PIT."""
    raw = results / "comparison/raw" / method / f"{label}.json"
    staged_json = work / "run-result.json"
    staged_json.write_text(json.dumps(result, indent=2, ensure_ascii=False, allow_nan=False) + "\n",
                           encoding="utf-8")
    staged = []
    for kind, report in (("coverage", coverage), ("mutation", mutation)):
        target = results / kind / method / label
        new = None
        if report is not None:
            if not (report / "index.html").is_file():
                raise FileNotFoundError(f"Missing HTML report in {report}")
            new = shutil.copytree(report, work / f"new-{kind}")
        staged.append((target, new))
    # Serialize publishers for the same run. Other run IDs proceed independently.
    lock = work.parent / f"{method}-{label}.lock"
    with lock.open("x", encoding="utf-8"):
        pass
    backups, installed = [], []
    try:
        for index, (target, new) in enumerate(staged):
            if target.exists():
                backup = work / f"previous-{index}"
                target.rename(backup)
                backups.append((target, backup))
            if new is not None:
                target.parent.mkdir(parents=True, exist_ok=True)
                new.rename(target)
                installed.append((target, new))
        raw.parent.mkdir(parents=True, exist_ok=True)
        staged_json.replace(raw)
    except BaseException:
        for target, new in reversed(installed):
            target.rename(new)
        for target, backup in reversed(backups):
            backup.rename(target)
        raise
    finally:
        lock.unlink()
    return raw


def measure(args):
    if not re.fullmatch(r"[a-zA-Z0-9][a-zA-Z0-9_-]*", args.method) or args.run < 1:
        raise ValueError("Expected a method code and a positive run number")
    with workdir(args.suite, program=args.program, keep=args.keep_workdir, root=ROOT) as work:
        if args.keep_workdir:
            print(f"Workdir: {work}", flush=True)
        reference = args.program == "reference"
        test = run_maven(work, ["-Djacoco.skip=true", "test"] if reference else ["test"])
        report = parse_surefire(work / "target/surefire-reports")
        failed = report["failures"] + report["errors"]
        passed = report["tests"] - failed - report["skipped"]
        print(f"{args.program}: passed={passed} failed={failed} skipped={report['skipped']}", flush=True)
        if test.returncode or failed or not passed:
            raise ValueError(f"Maven test exit {test.returncode}, failed tests: {report['failed_tests']}, "
                             f"executed: {passed + failed}\n{test.stdout}\n{test.stderr}")
        if reference:
            return

        config = load_config(ROOT / "experiments/config")
        generation = read_generation(args.suite, args.method, args.run)
        coverage = work / "target/site/jacoco"
        result = {"method": args.method, "run": args.run,
                  "tests": test_metrics(generation, report, passed, failed),
                  **parse_jacoco(coverage / "jacoco.xml"), "mutation": None,
                  "time": {"generation_s": generation.get("generation_s", 0.0), "execution_s": test.elapsed_s},
                  "llm": llm_metrics(generation, config["llm"])}
        if args.method in ("p4-coverage-guided", "p4b-mutation-feedback"):
            result["iterations"] = generation.get("iterations", [])

        results = ROOT / "results"
        mutation = None
        if args.mutation:
            settings = config["measure"]
            pit = run_maven(work, [f"-Dpit.mutators={','.join(settings['pit_mutators'])}",
                                   f"-Dpit.timeoutFactor={settings['timeout_factor']}",
                                   "org.pitest:pitest-maven:mutationCoverage"])
            if pit.returncode:
                raise ValueError(f"PIT exit {pit.returncode}\n{pit.stdout}\n{pit.stderr}")
            mutation = work / "target/pit-reports"
            result["mutation"] = parse_pit(mutation / "mutations.xml", results / "mutation/equivalent-mutants.csv")
            result["time"]["execution_s"] += pit.elapsed_s

        schema = json.loads((ROOT / "experiments/schema/run-result.schema.json").read_text(encoding="utf-8"))
        Draft202012Validator(schema).validate(result)
        raw = publish(work, results, args.method, f"run-{args.run:02d}", result, coverage, mutation)
        print(raw.relative_to(ROOT).as_posix())


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--suite", required=True, type=Path)
    parser.add_argument("--method", required=True)
    parser.add_argument("--run", required=True, type=int)
    parser.add_argument("--mutation", action="store_true")
    parser.add_argument("--program", choices=("subject", "reference"), default="subject")
    parser.add_argument("--keep-workdir", action="store_true")
    try:
        measure(parser.parse_args())
    except (OSError, ValueError, KeyError, TypeError, ET.ParseError, ValidationError, yaml.YAMLError) as exc:
        print(f"Measurement failed: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
