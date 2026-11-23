"""List undetected mutants of one measured run with their full key and source line.

Reads results/mutation/<method>/run-XX/mutations.xml and custom-mutations.json
(saved by measure.py). Each mutant that was not detected (SURVIVED,
NO_COVERAGE, or another non-killing status) is printed with the key columns of
results/mutation/equivalent-mutants.csv, so a reviewer can copy the line into
that file and append a reason.
"""

import argparse
from collections import Counter
import csv
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

TOOLS_DIR = Path(__file__).resolve().parent
if str(TOOLS_DIR) not in sys.path:
    sys.path.insert(0, str(TOOLS_DIR))

from svv.config import ROOT
from svv.pit import KEY, EquivalentMatcher, alive_mutants, load_equivalents, mutant_key, read_suite_results

CSV_COLUMNS = (*KEY, "reason")
TABLE_COLUMNS = (*KEY, "status", "operator", "file", "code")


def _source_file(source, row):
    """Path of the mutated file relative to the Maven project."""
    if source == "custom":
        return row["file"]
    package, _, simple = row["class"].rpartition(".")
    name = row.get("file") or f"{simple.split('$', 1)[0]}.java"
    return "/".join(["src/main/java", *filter(None, package.split(".")), name])


def _code_line(source_root, relative, line, cache):
    if relative not in cache:
        path = source_root / relative
        try:
            with path.open(encoding="utf-8", newline="") as stream:
                cache[relative] = stream.read().splitlines()
        except OSError:
            cache[relative] = None
    lines = cache[relative]
    if lines is None or not 1 <= line <= len(lines):
        return "<source unavailable>"
    return lines[line - 1].strip()


def undetected(suite_results, source_root, equivalents):
    """Undetected mutants of one run, each with key, status, file, code and CSV match flag."""
    pit, custom = read_suite_results(suite_results)
    alive = alive_mutants(pit, custom)
    originals = [("pit", row) for row in pit] + [("custom", row) for row in custom]
    by_key = {}
    for source, row in originals:
        by_key.setdefault(mutant_key(source, row), (source, row))
    keys = [tuple(str(item[name]) for name in KEY) for item in alive]
    duplicates = sorted(key for key, count in Counter(keys).items() if count > 1)
    if duplicates:
        raise ValueError(f"Mutant keys are not unique in {suite_results}: {duplicates}")
    is_equivalent = EquivalentMatcher(equivalents)
    cache = {}
    rows = []
    for item, key in zip(alive, keys):
        source, original = by_key[key]
        relative = _source_file(source, original)
        rows.append({**item, "file": relative, "code": _code_line(source_root, relative, item["line"], cache),
                     "equivalent": is_equivalent(key)})
    return rows


def _write_blocks(rows, suite_results, out):
    counts = Counter(row["status"] for row in rows)
    summary = ", ".join(f"{status} {count}" for status, count in sorted(counts.items())) or "none"
    print(f"# Undetected mutants in {suite_results.as_posix()}: {len(rows)} ({summary})", file=out)
    print("# Copy a CSV line into results/mutation/equivalent-mutants.csv and append the reason.", file=out)
    print(f"# {','.join(CSV_COLUMNS)}", file=out)
    writer = csv.writer(out, lineterminator="\n")
    for number, row in enumerate(rows, start=1):
        marker = " [already equivalent in CSV]" if row["equivalent"] else ""
        print(file=out)
        print(f"# [{number}] {row['status']} {row['source']} {row['operator']} "
              f"{row['file']}:{row['line']}{marker}", file=out)
        print(f"#     {row['code']}", file=out)
        writer.writerow([row[name] for name in KEY] + [""])


def _write_table(rows, out):
    writer = csv.writer(out, lineterminator="\n")
    writer.writerow([*TABLE_COLUMNS, "already_equivalent"])
    for row in rows:
        writer.writerow([row[name] for name in TABLE_COLUMNS] + ["yes" if row["equivalent"] else ""])


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--suite-results", required=True, type=Path,
                        help="results/mutation/<method>/run-XX directory")
    parser.add_argument("--source-root", type=Path, default=ROOT / "subject",
                        help="Maven project whose sources were mutated (default: subject)")
    parser.add_argument("--equivalents", type=Path, default=ROOT / "results/mutation/equivalent-mutants.csv",
                        help="mark mutants already listed in this CSV")
    parser.add_argument("--format", choices=("blocks", "csv"), default="blocks",
                        help="blocks: annotated, paste-ready CSV lines; csv: one table with code")
    args = parser.parse_args(argv)
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    try:
        equivalents = load_equivalents(args.equivalents) if args.equivalents.is_file() else []
        rows = undetected(args.suite_results, args.source_root, equivalents)
    except (OSError, ValueError, KeyError, ET.ParseError, json.JSONDecodeError) as exc:
        print(f"mutation_ids failed: {exc}", file=sys.stderr)
        return 1
    if args.format == "csv":
        _write_table(rows, sys.stdout)
    else:
        _write_blocks(rows, args.suite_results, sys.stdout)
    return 0


if __name__ == "__main__":
    sys.exit(main())
