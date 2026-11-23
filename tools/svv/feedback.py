"""Build source-grounded feedback from the same JaCoCo and mutation data as measure.py."""

from collections import defaultdict
from pathlib import Path

from .custom_mutation import _pairs, tokenize
from .pit import DETECTED, PIT_OPERATORS, EquivalentMatcher, load_equivalents, mutant_key
from .test_blocks import extract_block_from_java


def suite_blocks(suite: Path):
    """Read top-level test methods, including multi-method smoke suites, without rewriting files."""
    blocks = []
    for path in sorted(suite.rglob("*.java")):
        source = path.read_text(encoding="utf-8")
        tokens = list(tokenize(source))
        pairs = _pairs(tokens)
        body = next((i for i, token in enumerate(tokens) if token.value == "{"), None)
        if body is None:
            continue
        i = start = body + 1
        while i < pairs[body]:
            value = tokens[i].value
            if value in ("(", "["):
                i = pairs[i]
            elif value == "{":
                i = pairs[i]
                block = extract_block_from_java(source[tokens[start].offset:tokens[i].offset + 1])
                if block is not None:
                    blocks.append(block)
                start = i + 1
            elif value == ";":
                start = i + 1
            i += 1
    return blocks


def mutation_outcomes(pit, custom, equivalents):
    """Keep full stable identities for selection and the original custom edits for prompts."""
    equivalent = EquivalentMatcher(load_equivalents(equivalents))
    killed, live = set(), []
    for source, rows in (("pit", pit), ("custom", custom)):
        for row in rows:
            key = mutant_key(source, row)
            if equivalent(key):
                continue
            if row["status"] in DETECTED:
                killed.add(key)
            elif row["status"] in {"SURVIVED", "NO_COVERAGE"}:
                mutator = row["mutator"].rsplit(".", 1)[-1]
                operator = PIT_OPERATORS.get(mutator, "other") if source == "pit" else row["operator"]
                live.append({**row, "source": source, "operator": operator})
    return killed, live


def _source_lines(source_dir, cls):
    filename = cls.split("$", 1)[0].rsplit(".", 1)[-1] + ".java"
    return (source_dir / filename).read_text(encoding="utf-8").splitlines()


def coverage_feedback(uncovered: dict, source_dir: Path, context: int = 2) -> str:
    """Coalesce overlapping windows within a method and annotate each missed site once."""
    groups = defaultdict(lambda: defaultdict(list))
    for kind in ("lines", "branches"):
        for site in uncovered[kind]:
            notes = groups[site["class"], site["method"]][site["line"]]
            if kind == "lines":
                notes.append("uncovered line")
            else:
                notes.append(f"branches covered={site['covered']} missed={site['missed']}")

    sections = []
    for (cls, method), sites in sorted(groups.items()):
        lines = _source_lines(source_dir, cls)
        numbers = set()
        for nr in sites:
            if not 1 <= nr <= len(lines):
                raise ValueError(f"JaCoCo line {nr} outside source for {cls}")
            numbers.update(range(max(1, nr - context), min(len(lines), nr + context) + 1))
        snippet = [f"{cls}.{method or '(unknown)'}"]
        previous = None
        for nr in sorted(numbers):
            if previous is not None and nr > previous + 1:
                snippet.append("  ...")
            note = "  // " + ", ".join(sites[nr]) if nr in sites else ""
            snippet.append(f"  {nr}: {lines[nr - 1]}{note}")
            previous = nr
        sections.append("\n".join(snippet))
    return "\n\n".join(sections) or "(no uncovered sites)"


def mutation_feedback(live: list[dict], source_dir: Path) -> str:
    groups = defaultdict(list)
    for row in live:
        groups[row["class"], row["method"]].append(row)
    sections = []
    for (cls, method), rows in sorted(groups.items()):
        lines = _source_lines(source_dir, cls)
        section = [f"{cls}.{method or '(unknown)'}"]
        for row in sorted(rows, key=lambda r: (r["line"], mutant_key(r["source"], r))):
            nr = row["line"]
            source = lines[nr - 1].strip() if 1 <= nr <= len(lines) else "(no source line)"
            section.append(f"  {nr}: {source}")
            section.append(f"    {row['source']} {row['status']} {row['operator']} "
                           f"({row['mutator']}): {row['description']}")
            if row["source"] == "custom":
                section.append(f"    original: {row['original']!r} -> replacement: {row['mutated']!r}")
        sections.append("\n".join(section))
    return "\n\n".join(sections) or "(no surviving targets)"
