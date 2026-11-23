"""Extend the paired P3/P4 suite with coverage-guided or mutation-guided JUnit tests."""

import argparse
from collections import Counter
from datetime import datetime, timezone
from hashlib import sha256
import json
from pathlib import Path
import re
import shutil
import sys
from time import perf_counter
import xml.etree.ElementTree as ET

import yaml

if __package__ in (None, ""):
    sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from tools.generate_p3 import METHOD as P3, SPEC_ONLY_METHOD as P3R, _llm_timeout_override, _ordered_spec_paths
from tools.svv import llm_client
from tools.svv.config import ROOT, load_config
from tools.svv.custom_mutation import run_custom
from tools.svv.feedback import coverage_feedback, mutation_feedback as render_mutants, mutation_outcomes, suite_blocks
from tools.svv.jacoco import parse_jacoco
from tools.svv.maven import run_maven
from tools.svv.pit import merge_mutations, read_pit_mutants
from tools.svv.selection import Measurement, select_greedy
from tools.svv.surefire import parse_surefire
from tools.svv.test_blocks import extract_blocks
from tools.svv.test_filter import _stage_dir, filter_tests, write_suite
from tools.svv.workdir import workdir

P4 = "p4-coverage-guided"
P4B = "p4b-mutation-feedback"
P4R = "p4r-coverage-guided"
P4BR = "p4br-mutation-feedback"
CHAINS = {"full": (P3, P4, P4B), "reduced": (P3R, P4R, P4BR)}
TEST_COUNTS = ("generated", "compiled", "passed_reference", "valid")
USAGE = ("calls", "input_tokens", "output_tokens", "cached_input_tokens",
         "reasoning_output_tokens", "tool_events", "duration_s")


def measure_suite(suite, *, root, mutation, settings):
    """Measure a clean, isolated copy. No intermediate candidate report is published."""
    with workdir(suite, root=root) as project:
        test = run_maven(project, ["test"])
        report = parse_surefire(project / "target/surefire-reports")
        if (test.returncode or report["failures"] or report["errors"]
                or report["tests"] <= report["skipped"]):
            raise ValueError(f"Maven test exit {test.returncode}: {report}\n{test.stdout}\n{test.stderr}")
        measured = Measurement(**parse_jacoco(project / "target/site/jacoco/jacoco.xml"))
        if mutation:
            pit = run_maven(project, [f"-Dpit.mutators={','.join(settings['pit_mutators'])}",
                                      f"-Dpit.timeoutFactor={settings['timeout_factor']}",
                                      "org.pitest:pitest-maven:mutationCoverage"])
            if pit.returncode:
                raise ValueError(f"PIT exit {pit.returncode}\n{pit.stdout}\n{pit.stderr}")
            rows = list(read_pit_mutants(project / "target/pit-reports/mutations.xml"))
            custom = run_custom(project, suite, test.elapsed_s, settings, root=root)
            equivalents = root / "results/mutation/equivalent-mutants.csv"
            measured.killed, measured.live = mutation_outcomes(rows, custom, equivalents)
            measured.mutation_score = merge_mutations(rows, custom, equivalents)["score"]
        return measured


def render_prompt(root, mutation, existing, measured, *, variant="full"):
    if variant not in CHAINS:
        raise ValueError(f"Unknown P4 variant: {variant}")
    directory = "mutation-feedback" if mutation else "coverage-guided"
    if variant == "reduced":
        directory += "-reduced"
    template = (root / "prompts" / directory / "generate.md").read_text(encoding="utf-8")
    spec_dir = root / "docs/spec"
    source_dir = root / "subject/src/main/java/shop"
    spec = [spec_dir / "api.md", *_ordered_spec_paths(spec_dir)]
    sections = {
        "SPEC": "\n\n".join(f"### {p.name}\n\n{p.read_text(encoding='utf-8').strip()}" for p in spec),
        "TEST_NAMES": "\n".join(block.method_name for block in existing),
        "FEEDBACK": (render_mutants(measured.live, source_dir) if mutation
                     else coverage_feedback(measured.uncovered, source_dir)),
    }
    if variant == "full":
        sections["SOURCE"] = "\n\n".join(
            f"### {p.name}\n\n```java\n{p.read_text(encoding='utf-8').strip()}\n```"
            for p in sorted(source_dir.glob("*.java"))
        )
    pattern = r"\{\{([A-Z_]+)\}\}"
    if set(re.findall(pattern, template)) != sections.keys():
        raise ValueError(f"Incorrect placeholders in {directory}/generate.md")
    return re.sub(pattern, lambda match: sections[match[1]], template)


def paired_generation(suite, method, run):
    path = suite / "generation.json"
    if not path.is_file() or not any(suite.rglob("*.java")):
        raise ValueError(f"Missing paired {method} run-{run:02d}: expected generation.json and Java tests in {suite}")
    data = json.loads(path.read_text(encoding="utf-8"))
    if data["method"] != method or data["run"] != run:
        raise ValueError(f"{path}: expected {method} run-{run:02d}")
    # A missing parent cost would silently understate the paired experiment.
    for field in ("generation_s", "llm", "tests"):
        if field not in data:
            raise ValueError(f"{path}: missing {field}")
    for field in ("calls", "input_tokens", "output_tokens"):
        if field not in data["llm"]:
            raise ValueError(f"{path}: missing llm.{field}")
    return data


def stopping_reason(settings, metrics, *, mutation, iterations, tokens, no_gain):
    target = "target_mutation_score" if mutation else "target_branch_coverage"
    if metrics["mutation_score" if mutation else "branch_pct"] >= settings[target]:
        return target
    if tokens >= settings["token_budget"]:
        return "token_budget"
    if no_gain >= settings["stop_if_no_gain_iterations"]:
        return "stop_if_no_gain_iterations"
    if iterations >= settings["max_iterations"]:
        return "max_iterations"
    return None


def _combined_usage(first, second):
    result = {key: first.get(key, 0) + second.get(key, 0) for key in USAGE}
    result["total_tokens"] = result["input_tokens"] + result["output_tokens"]
    costs = [item.get("cost_usd") for item in (first, second) if item.get("calls")]
    result["cost_usd"] = None if None in costs else sum(costs)
    for field in ("provider", "model", "effort"):
        result[field] = (second if second.get("calls") else first).get(field, "")
    return result


def _publish(stage, target, scratch, record):
    """Only replace an empty destination or a previous complete output of the same run."""
    target.parent.mkdir(parents=True, exist_ok=True)
    lock_name = sha256(str(target).casefold().encode("utf-8")).hexdigest()
    lock = scratch.parent / f"publish-{lock_name}.lock"
    with lock.open("x", encoding="utf-8"):
        pass
    try:
        backup = scratch / "previous"
        if target.exists():
            if any(target.iterdir()):
                previous = paired_generation(target, record["method"], record["run"])
                managed = {"generation.json", *previous.get("suite_files", [])}
                if any(p.relative_to(target).as_posix() not in managed
                       for p in target.rglob("*") if p.is_file()):
                    raise ValueError(f"Output contains files outside the previous generation: {target}")
            target.rename(backup)
        try:
            stage.rename(target)
        except BaseException:
            if backup.exists():
                backup.rename(target)
            raise
    finally:
        lock.unlink()


def generate(run, *, variant="full", mutation_feedback=False, demo=False, output=None, dry_run=False,
             provider=None, model=None, effort=None, root=ROOT):
    if type(run) is not int or run < 1:
        raise ValueError("run must be a positive integer")
    root = root.resolve()
    mutation = mutation_feedback
    if variant not in CHAINS:
        raise ValueError(f"Unknown P4 variant: {variant}")
    direct, coverage, mutants = CHAINS[variant]
    parent, method = (coverage, mutants) if mutation else (direct, coverage)
    label = f"run-{run:02d}"
    parent_dir = root / "test-suites" / parent / label
    inherited = paired_generation(parent_dir, parent, run)
    cfg_dir = root / "experiments/config"
    config = load_config(cfg_dir)
    p4 = config["p4"]
    settings = dict(p4["p4b"] if mutation else p4)
    if demo:
        settings["max_iterations"] = p4["demo_max_iterations"]
    target = (output or root / (".work/demo" if demo else "test-suites") / method / label).resolve()
    if target.is_relative_to(parent_dir) or parent_dir.is_relative_to(target):
        raise ValueError(f"Output overlaps paired input: {target}")

    def measure(suite):
        return measure_suite(suite, root=root, mutation=mutation, settings=config["measure"])

    start = perf_counter()
    started_at = datetime.now(timezone.utc).isoformat()
    with _stage_dir(root, "p4-") as scratch:
        suite = scratch / "suite"
        for src in sorted(parent_dir.rglob("*.java")):
            dest = suite / src.relative_to(parent_dir)
            dest.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dest)
        existing = suite_blocks(suite)
        current = measure(suite)
        baseline = current.metrics()
        if dry_run:
            prompt = render_prompt(root, mutation, existing, current, variant=variant)
            print(prompt, end="\n")
            print(f"[dry-run] method={method} {label} chars={len(prompt)} seed={parent} output={target}")
            return None

        def evaluate(items):
            # Fresh Maven project per trial. The staged suite contains only permanent winners.
            paths = [suite / f"{item.class_name}.java" for item in items]
            if any(path.exists() for path in paths):
                raise ValueError("Candidate class name collides with a retained test")
            try:
                write_suite(items, method=method, run=run, output_dir=suite)
                return measure(suite)
            finally:
                for path in paths:
                    path.unlink(missing_ok=True)

        incremental = _combined_usage({}, {})
        tests = {key: inherited["tests"][key] for key in TEST_COUNTS}
        tests["dropped"] = list(inherited["tests"].get("dropped", []))
        iterations = []
        selection_drops = []
        no_gain = 0
        next_index = 1
        while True:
            reason = stopping_reason(settings, current.metrics(), mutation=mutation, iterations=len(iterations),
                                     tokens=incremental["total_tokens"],
                                     no_gain=no_gain)
            if reason:
                break
            i = len(iterations) + 1
            before = current.metrics()
            prompt = render_prompt(root, mutation, existing, current, variant=variant)
            with _llm_timeout_override(p4["timeout_s"], config_dir=cfg_dir):
                response = llm_client.complete(prompt, method=method, run=run,
                                               purpose=f"{'demo-' if demo else ''}{method}-iteration-{i}",
                                               provider=provider, model=model, effort=effort)
            incremental = _combined_usage(incremental, {**vars(response), "calls": 1})
            candidates = extract_blocks(response.text)
            filtered = filter_tests(candidates, existing=existing, class_prefix="P4b" if mutation else "P4",
                                    start_index=next_index, root=root)
            next_index += len(candidates)
            selected, current = select_greedy(filtered.kept, current, evaluate)
            write_suite(selected, method=method, run=run, output_dir=suite)
            existing.extend(item.block for item in selected)
            for key in TEST_COUNTS:
                tests[key] += getattr(filtered, key)
            tests["dropped"].extend({"name": d.name, "reason": d.reason} for d in filtered.dropped)
            drops = Counter(d.reason for d in filtered.dropped)
            if len(selected) < filtered.valid:
                reason = "no_mutation_gain" if mutation else "no_coverage_gain"
                drops[reason] = filtered.valid - len(selected)
                kept_classes = {item.class_name for item in selected}
                selection_drops.extend({"i": i, "name": item.block.method_name, "reason": reason}
                                       for item in filtered.kept if item.class_name not in kept_classes)
            after = current.metrics()
            iterations.append({"i": i, **after, "kept_tests": len(selected),
                               "tokens": response.input_tokens + response.output_tokens,
                               "generated": filtered.generated, "valid": filtered.valid,
                               "dropped_by_reason": dict(drops)})
            no_gain = 0 if selected else no_gain + 1
            if demo:
                change = ", ".join(f"{k}={v:.2f} -> {after[k]:.2f}" for k, v in before.items())
                print(f"iteration {i}: {change}, kept_tests={len(selected)}", flush=True)

        elapsed = perf_counter() - start
        record = {
            "method": method, "run": run, "started_at": started_at,
            "ended_at": datetime.now(timezone.utc).isoformat(), "stop_reason": reason,
            "generation_s": inherited["generation_s"] + elapsed,
            "llm": _combined_usage(inherited["llm"], incremental),
            "inherited": {"method": parent, "run": run, "generation_s": inherited["generation_s"],
                          "llm": inherited["llm"]},
            "incremental": {"generation_s": elapsed, "llm": incremental,
                            "dropped_by_selection": selection_drops},
            "baseline": baseline, "final": current.metrics(), "iterations": iterations, "tests": tests,
            "suite_files": sorted(p.relative_to(suite).as_posix() for p in suite.rglob("*.java")),
        }
        (suite / "generation.json").write_text(json.dumps(record, ensure_ascii=False, indent=2,
                                                         allow_nan=False) + "\n", encoding="utf-8")
        _publish(suite, target, scratch, record)
        return record


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run", required=True, type=int)
    parser.add_argument("--variant", choices=CHAINS, default="full")
    parser.add_argument("--mutation-feedback", action="store_true")
    parser.add_argument("--demo", action="store_true", help="Use the demo iteration limit and print progress")
    parser.add_argument("--output", type=Path, help="Destination directory of the final Java files and metadata")
    parser.add_argument("--dry-run", action="store_true", help="Measure the seed and print the first prompt")
    parser.add_argument("--provider", choices=("codex-cli", "agy-cli", "anthropic"))
    parser.add_argument("--model")
    parser.add_argument("--effort")
    args = parser.parse_args(argv)
    try:
        record = generate(**vars(args), root=ROOT)
    except llm_client.LlmQuotaExceeded as exc:
        print(f"Quota exceeded: {exc}", file=sys.stderr)
        return 2
    except (llm_client.LlmError, OSError, ValueError, KeyError, ET.ParseError, yaml.YAMLError) as exc:
        print(f"P4 generation failed: {exc}", file=sys.stderr)
        return 1
    if record:
        print(f"{record['method']} run-{args.run:02d}: {len(record['iterations'])} iterations, "
              f"stop={record['stop_reason']}, final={record['final']}")
    return 0


if __name__ == "__main__":
    for stream in (sys.stdout, sys.stderr):
        if hasattr(stream, "reconfigure"):
            stream.reconfigure(encoding="utf-8", errors="replace")
    sys.exit(main())
