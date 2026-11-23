"""List D-012 source mutants, or evaluate them on a suite and write a JSON report."""

import argparse
from collections import Counter
import json
from pathlib import Path
import sys
from uuid import uuid4
import xml.etree.ElementTree as ET

TOOLS_DIR = Path(__file__).resolve().parent
if str(TOOLS_DIR) not in sys.path:
    sys.path.insert(0, str(TOOLS_DIR))

from svv.config import ROOT, load_config
from svv.custom_mutation import OPERATORS, enumerate_mutants, run_custom
from svv.maven import run_maven
from svv.surefire import parse_surefire
from svv.workdir import workdir


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--list", action="store_true", help="print candidates and operator counts")
    mode.add_argument("--suite", type=Path, help="evaluate candidates using this test suite")
    parser.add_argument("--project", type=Path, default=ROOT / "subject", help="project directory for --list")
    parser.add_argument("--output", type=Path, help="JSON report path for --suite")
    args = parser.parse_args(argv)
    if args.list:
        candidates = [m.record() for m in enumerate_mutants(args.project)]
        counts = Counter(m["operator"] for m in candidates)
        print(json.dumps({"mutants": candidates, "by_operator": {op: counts[op] for op in OPERATORS}},
                         indent=2))
        return 0
    if not args.output:
        parser.error("--suite requires --output")
    try:
        with workdir(args.suite) as baseline:
            build = run_maven(baseline, ["test"])
            report = parse_surefire(baseline / "target/surefire-reports")
            if build.returncode or report["failures"] or report["errors"] or report["tests"] <= report["skipped"]:
                raise ValueError(f"Baseline suite must pass\n{build.stdout}\n{build.stderr}")
            rows = run_custom(baseline, args.suite, build.elapsed_s, load_config()["measure"])
            args.output.parent.mkdir(parents=True, exist_ok=True)
            staged = args.output.with_name(f".{args.output.name}.{uuid4().hex}.tmp")
            try:
                staged.write_text(json.dumps(rows, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
                staged.replace(args.output)
            except BaseException:
                staged.unlink(missing_ok=True)
                raise
    except (ValueError, OSError, ET.ParseError) as exc:
        print(exc, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
