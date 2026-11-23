"""PIT reports and the common scoring convention for PIT and source mutants."""

import csv
from collections import Counter
from collections.abc import Iterable, Iterator, Mapping
from itertools import chain
from pathlib import Path
import xml.etree.ElementTree as ET


PIT_OPERATORS = {
    "ConditionalsBoundaryMutator": "relational",
    "CONDITIONALS_BOUNDARY": "relational",
    "MathMutator": "arithmetic",
    "MATH": "arithmetic",
    "VoidMethodCallMutator": "statement_deletion",
    "VOID_METHOD_CALLS": "statement_deletion",
    "MemberVariableMutator": "statement_deletion",
    "EXPERIMENTAL_MEMBER_VARIABLE": "statement_deletion",
}
OPERATOR_SOURCES = {
    "relational": "pit", "arithmetic": "pit", "conditional": "custom",
    "boolean_constant": "custom", "statement_deletion": "pit",
}
IDENTITY = ("class", "method", "line", "mutator", "description")
DETECTED = {"KILLED", "TIMED_OUT", "MEMORY_ERROR"}


def read_pit_mutants(path: Path) -> Iterator[dict]:
    """Yield individual mutants, retaining the full mutator name for CSV matching."""
    fields = {"class": "mutatedClass", "method": "mutatedMethod", "line": "lineNumber",
              "mutator": "mutator", "description": "description"}
    for node in ET.parse(path).findall("mutation"):
        row = {key: node.findtext(tag) for key, tag in fields.items()}
        if None in row.values():
            raise ValueError(f"Incomplete mutation in {path}: {row}")
        yield {**row, "line": int(row["line"]), "status": node.attrib["status"]}


def merge_mutations(pit: Iterable[Mapping], custom: Iterable[Mapping] = (),
                    equivalents: Path | None = None) -> dict:
    """Combine individual records from both sources before computing the score.

    Both inputs have class, method, line, mutator, description, status. For
    custom mutants, mutator is the operator key (or supply operator separately).
    CSV matches the five identity fields within the indicated source.

    Per PIT convention TIMED_OUT and MEMORY_ERROR count as killed. NO_COVERAGE,
    SURVIVED and other statuses (e.g. RUN_ERROR) stay in the denominator without
    a kill. status_counts preserves every original status, including equivalents.
    Custom COMPILE_ERROR is an invalid candidate, recorded only in status_counts.
    Equivalent mutants count in total but contribute neither a kill nor a trial.
    A zero denominator yields 0.0. Operator source records observed provenance,
    or the designated producer when there are no mutants of that operator.
    """
    excluded = set()
    if equivalents is not None:
        with equivalents.open(encoding="utf-8", newline="") as stream:
            for row in csv.DictReader(stream):
                excluded.add((row["source"], *(row[key] for key in IDENTITY)))

    by_operator = {key: {"source": source, "total": 0, "killed": 0}
                   for key, source in OPERATOR_SOURCES.items()}
    sources = {}
    counts = Counter()
    total = killed = equivalent = 0
    records = chain((("pit", row) for row in pit), (("custom", row) for row in custom))
    for source, row in records:
        status = row["status"]
        counts[status] += 1
        if source == "custom" and status == "COMPILE_ERROR":
            continue
        if source == "pit":
            operator = PIT_OPERATORS.get(row["mutator"].rsplit(".", 1)[-1], "other")
        else:
            operator = row.get("operator", row["mutator"])
            if operator not in ("conditional", "boolean_constant", "statement_deletion"):
                operator = "other"
        entry = by_operator.setdefault(operator, {"total": 0, "killed": 0})
        sources.setdefault(operator, set()).add(source)
        total += 1
        entry["total"] += 1
        if (source, *(str(row[key]) for key in IDENTITY)) in excluded:
            equivalent += 1
        elif status in DETECTED:
            killed += 1
            entry["killed"] += 1

    for operator, observed in sources.items():
        by_operator[operator]["source"] = ["pit", "custom"] if len(observed) == 2 else next(iter(observed))
    denominator = total - equivalent
    return {"total": total, "killed": killed, "survived": counts["SURVIVED"],
            "no_coverage": counts["NO_COVERAGE"], "timed_out": counts["TIMED_OUT"],
            "equivalent": equivalent, "score": 100 * killed / denominator if denominator else 0.0,
            "by_operator": by_operator, "status_counts": dict(counts)}


def parse_pit(path: Path, equivalents: Path | None = None) -> dict:
    return merge_mutations(read_pit_mutants(path), equivalents=equivalents)
