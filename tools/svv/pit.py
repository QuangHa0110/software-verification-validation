"""PIT reports and the common scoring convention for PIT and source mutants."""

import csv
from collections import Counter
from collections.abc import Iterable, Iterator, Mapping
from itertools import chain
import json
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
    "BooleanTrueReturnValsMutator": "boolean_constant",
    "TRUE_RETURNS": "boolean_constant",
    "BooleanFalseReturnValsMutator": "boolean_constant",
    "FALSE_RETURNS": "boolean_constant",
}
OPERATOR_SOURCES = {
    "relational": "pit", "arithmetic": "pit", "conditional": "custom",
    "boolean_constant": ["pit", "custom"], "statement_deletion": "pit",
}
IDENTITY = ("class", "method", "line", "mutator", "index", "description")
KEY = ("source", *IDENTITY)
DETECTED = {"KILLED", "TIMED_OUT", "MEMORY_ERROR"}
CSV_REQUIRED = ("source", "class", "method", "line", "mutator", "description", "reason")
_INDEX = KEY.index("index")


def _identity(row: Mapping) -> tuple[str, ...]:
    return tuple(str(row[key]) for key in IDENTITY)


def read_pit_mutants(path: Path, *, full_matrix: bool = False) -> Iterator[dict]:
    """Yield individual mutants, retaining the full mutator name for CSV matching.

    index is the bytecode instruction index from <indexes> (";"-joined when PIT
    lists several). If two mutants of one report still share every identity
    field, "@" and their ";"-joined <blocks> are appended to make each unique.
    Reports without <indexes> (hand-written fixtures) yield an empty index.
    """
    fields = {"class": "mutatedClass", "method": "mutatedMethod", "line": "lineNumber",
              "mutator": "mutator", "description": "description"}
    rows = []
    for node in ET.parse(path).findall("mutation"):
        row = {key: node.findtext(tag) for key, tag in fields.items()}
        if None in row.values():
            raise ValueError(f"Incomplete mutation in {path}: {row}")
        if full_matrix:
            killers = node.findtext("killingTests")
            if killers is None or (node.attrib["status"] == "KILLED" and not killers):
                raise ValueError(f"Missing PIT killingTests in matrix report {path}")
            row["killing_tests"] = killers.split("|") if killers else []
        index = ";".join((item.text or "").strip() for item in node.findall("indexes/index"))
        blocks = ";".join((item.text or "").strip() for item in node.findall("blocks/block"))
        rows.append({**row, "line": int(row["line"]), "index": index, "blocks": blocks,
                     "file": node.findtext("sourceFile") or "", "status": node.attrib["status"]})
    if any(row["index"] for row in rows):
        seen = Counter(_identity(row) for row in rows)
        for row in rows:
            if seen[_identity(row)] > 1:
                row["index"] = f"{row['index']}@{row['blocks']}"
        if len({_identity(row) for row in rows}) != len(rows):
            raise ValueError(f"Mutants in {path} are not distinguishable by index and blocks")
    yield from rows


def mutant_key(source: str, row: Mapping) -> tuple[str, ...]:
    """Full identity of a PIT or custom mutant in KEY order, as CSV strings.

    A custom mutant's index is its stable id (file:line:column:operator).
    """
    index = row.get("index")
    if index is None:
        index = row.get("id", "") if source == "custom" else ""
    return (source, *_identity({**row, "index": index}))


def load_equivalents(path: Path | None) -> list[dict]:
    """Rows of equivalent-mutants.csv. A missing or empty index column matches every index."""
    if path is None:
        return []
    with path.open(encoding="utf-8", newline="") as stream:
        reader = csv.DictReader(stream)
        missing = [name for name in CSV_REQUIRED if name not in (reader.fieldnames or [])]
        if missing:
            raise ValueError(f"{path}: missing columns {missing}")
        return [{**row, "index": row.get("index") or ""} for row in reader]


class EquivalentMatcher:
    """Match mutant keys against CSV rows on every column except reason."""

    def __init__(self, rows: Iterable[Mapping]):
        self.exact = set()
        self.any_index = set()
        for row in rows:
            key = tuple(str(row[name]) for name in KEY)
            if key[_INDEX]:
                self.exact.add(key)
            else:
                self.any_index.add(key[:_INDEX] + key[_INDEX + 1:])

    def __call__(self, key: tuple[str, ...]) -> bool:
        return key in self.exact or key[:_INDEX] + key[_INDEX + 1:] in self.any_index


def _records(pit: Iterable[Mapping], custom: Iterable[Mapping]):
    return chain((("pit", row) for row in pit), (("custom", row) for row in custom))


def _operator(source: str, row: Mapping) -> str:
    if source == "pit":
        return PIT_OPERATORS.get(row["mutator"].rsplit(".", 1)[-1], "other")
    operator = row.get("operator", row["mutator"])
    return operator if operator in ("conditional", "boolean_constant", "statement_deletion") else "other"


def merge_mutations(pit: Iterable[Mapping], custom: Iterable[Mapping] = (),
                    equivalents: Path | None = None) -> dict:
    """Combine individual records from both sources before computing the score.

    Both inputs have class, method, line, mutator, description, status and
    optionally index (custom mutants fall back to their id). For custom
    mutants, mutator is the operator key (or supply operator separately).
    CSV matches every column except reason within the indicated source; an
    empty CSV index matches any index.

    Per PIT convention TIMED_OUT and MEMORY_ERROR count as killed. NO_COVERAGE,
    SURVIVED and other statuses (e.g. RUN_ERROR) stay in the denominator without
    a kill. status_counts preserves every original status, including equivalents.
    Custom COMPILE_ERROR is an invalid candidate, recorded only in status_counts.
    Equivalent mutants count in total but contribute neither a kill nor a trial.
    A zero denominator yields 0.0. Operator source records observed provenance,
    or the designated producer when there are no mutants of that operator.
    """
    is_equivalent = EquivalentMatcher(load_equivalents(equivalents))

    by_operator = {key: {"source": list(source) if isinstance(source, list) else source, "total": 0, "killed": 0}
                   for key, source in OPERATOR_SOURCES.items()}
    sources = {}
    counts = Counter()
    total = killed = equivalent = 0
    for source, row in _records(pit, custom):
        status = row["status"]
        counts[status] += 1
        if source == "custom" and status == "COMPILE_ERROR":
            continue
        operator = _operator(source, row)
        entry = by_operator.setdefault(operator, {"total": 0, "killed": 0})
        sources.setdefault(operator, set()).add(source)
        total += 1
        entry["total"] += 1
        if is_equivalent(mutant_key(source, row)):
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


def alive_mutants(pit: Iterable[Mapping], custom: Iterable[Mapping] = ()) -> list[dict]:
    """Undetected mutants (SURVIVED, NO_COVERAGE, RUN_ERROR, ...) with their full key.

    Stored as mutation.alive so a later equivalent-mutants.csv can be applied
    without measuring again (see rescore). Custom COMPILE_ERROR is excluded,
    as in merge_mutations.
    """
    alive = []
    for source, row in _records(pit, custom):
        status = row["status"]
        if status in DETECTED or (source == "custom" and status == "COMPILE_ERROR"):
            continue
        key = dict(zip(KEY, mutant_key(source, row)))
        alive.append({**key, "line": int(key["line"]), "status": status,
                      "operator": _operator(source, row)})
    return alive


def rescore(mutation: Mapping, equivalents: Iterable[Mapping]) -> dict:
    """Recompute a stored mutation result with the current equivalent-mutants.csv rows.

    Requires mutation["alive"]. Every mutant outside alive was detected and
    counts as killed; alive mutants matching a CSV row become equivalent. The
    result equals merge_mutations whenever the CSV lists only undetected
    mutants (an equivalent mutant cannot be killed).
    """
    alive = mutation["alive"]
    is_equivalent = EquivalentMatcher(equivalents)
    total = mutation["total"]
    if len(alive) > total:
        raise ValueError("mutation.alive lists more mutants than mutation.total")
    equivalent = sum(is_equivalent(tuple(str(item[key]) for key in KEY)) for item in alive)
    by_operator = {name: dict(entry) for name, entry in mutation["by_operator"].items()}
    alive_by_operator = Counter(item["operator"] for item in alive)
    for name, count in alive_by_operator.items():
        if count > by_operator.get(name, {"total": 0})["total"]:
            raise ValueError(f"mutation.alive does not match by_operator.{name}")
    for name, entry in by_operator.items():
        entry["killed"] = entry["total"] - alive_by_operator[name]
    killed = total - len(alive)
    denominator = total - equivalent
    return {**mutation, "killed": killed, "equivalent": equivalent,
            "score": 100 * killed / denominator if denominator else 0.0, "by_operator": by_operator}


def read_suite_results(directory: Path) -> tuple[list[dict], list[dict]]:
    """PIT and custom mutants saved by measure.py in results/mutation/<method>/run-XX.

    custom-mutations.json is optional (custom mutation disabled).
    """
    pit = list(read_pit_mutants(directory / "mutations.xml"))
    custom_path = directory / "custom-mutations.json"
    custom = json.loads(custom_path.read_text(encoding="utf-8")) if custom_path.is_file() else []
    if not isinstance(custom, list):
        raise ValueError(f"{custom_path}: expected a JSON list")
    return pit, custom


def parse_pit(path: Path, equivalents: Path | None = None) -> dict:
    return merge_mutations(read_pit_mutants(path), equivalents=equivalents)
