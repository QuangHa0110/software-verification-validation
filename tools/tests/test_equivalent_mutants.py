"""Equivalent mutant identity (index column), mutation.alive, rescoring and mutation_ids CLI."""

import csv
import io
import json
from pathlib import Path
import shutil
from uuid import uuid4

from jsonschema import Draft202012Validator
import pytest

from experiments.scripts import analyze
from tools import mutation_ids
from tools.svv.pit import (alive_mutants, load_equivalents, merge_mutations, read_pit_mutants,
                           read_suite_results, rescore)

ROOT = analyze.ROOT
FIXTURES = Path(__file__).parent / "fixtures"
SCHEMA = json.loads((ROOT / "experiments/schema/run-result.schema.json").read_text(encoding="utf-8"))
HEADER = "source,class,method,line,mutator,index,description,reason\n"
BOUNDARY = "org.pitest.mutationtest.engine.gregor.mutators.ConditionalsBoundaryMutator"
MATH = "org.pitest.mutationtest.engine.gregor.mutators.MathMutator"
SOURCE = """package shop;

public class PricingService {
    public long calculateDiscount(long subtotal, long max) {
        long discount = subtotal / 10;
        if (max > 0 && discount > max) {
            discount = max;
        }
        return discount + 0;
    }
}
"""


def _mutation(status, line, mutator, indexes, blocks, description="changed conditional boundary",
              method="calculateDiscount"):
    index_xml = "".join(f"<index>{i}</index>" for i in indexes)
    block_xml = "".join(f"<block>{b}</block>" for b in blocks)
    return (f"<mutation detected='{str(status == 'KILLED').lower()}' status='{status}' numberOfTestsRun='1'>"
            f"<sourceFile>PricingService.java</sourceFile><mutatedClass>shop.PricingService</mutatedClass>"
            f"<mutatedMethod>{method}</mutatedMethod><methodDescription>(JJ)J</methodDescription>"
            f"<lineNumber>{line}</lineNumber><mutator>{mutator}</mutator>"
            f"<indexes>{index_xml}</indexes><blocks>{block_xml}</blocks><killingTest/>"
            f"<description>{description}</description></mutation>")


PIT_XML = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<mutations partial=\"true\">\n"
           + "\n".join([
               _mutation("KILLED", 6, BOUNDARY, [80], [21]),
               _mutation("SURVIVED", 6, BOUNDARY, [85], [23]),
               _mutation("KILLED", 9, MATH, [90], [25], "Replaced long addition with subtraction"),
               _mutation("NO_COVERAGE", 9, MATH, [88], [24], "Replaced long subtraction with addition"),
           ])
           + "\n</mutations>\n")

CUSTOM = [
    {"id": "src/main/java/shop/PricingService.java:6:20:conditional", "file": "src/main/java/shop/PricingService.java",
     "class": "shop.PricingService", "method": "calculateDiscount", "line": 6, "operator": "conditional",
     "mutator": "conditional", "original": "&&", "mutated": "||", "description": "&& -> || (column 20)",
     "status": "SURVIVED"},
    {"id": "src/main/java/shop/PricingService.java:7:13:statement_deletion",
     "file": "src/main/java/shop/PricingService.java", "class": "shop.PricingService",
     "method": "calculateDiscount", "line": 7, "operator": "statement_deletion", "mutator": "statement_deletion",
     "original": "discount = max;", "mutated": "", "description": "delete discount = max; (column 13)",
     "status": "KILLED"},
    {"id": "src/main/java/shop/PricingService.java:9:9:statement_deletion",
     "file": "src/main/java/shop/PricingService.java", "class": "shop.PricingService",
     "method": "calculateDiscount", "line": 9, "operator": "statement_deletion", "mutator": "statement_deletion",
     "original": "x = 1;", "mutated": "", "description": "delete x = 1; (column 9)", "status": "COMPILE_ERROR"},
]
EQUIVALENT_85 = f"pit,shop.PricingService,calculateDiscount,6,{BOUNDARY},85,changed conditional boundary,max > 0 holds\n"


@pytest.fixture
def workspace():
    path = ROOT / ".work" / f"test-equiv-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def _write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8", newline="")
    return path


def _suite_results(directory, custom=CUSTOM):
    _write(directory / "mutations.xml", PIT_XML)
    if custom is not None:
        _write(directory / "custom-mutations.json", json.dumps(custom, indent=2) + "\n")
    return directory


def test_same_line_same_mutator_only_marked_index_is_excluded(workspace):
    pit = list(read_pit_mutants(_write(workspace / "mutations.xml", PIT_XML)))
    assert [(m["line"], m["index"], m["status"]) for m in pit[:2]] == [(6, "80", "KILLED"), (6, "85", "SURVIVED")]
    csv_path = _write(workspace / "eq.csv", HEADER + EQUIVALENT_85)

    result = merge_mutations(pit, CUSTOM, csv_path)
    # 4 PIT + 2 valid custom; killed: PIT index 80, PIT line 9, custom deletion.
    assert (result["total"], result["equivalent"], result["killed"]) == (6, 1, 3)
    assert result["score"] == pytest.approx(100 * 3 / 5)
    assert result["by_operator"]["relational"] == {"source": "pit", "total": 2, "killed": 1}

    # Marking the killed sibling (index 80) must not exclude the surviving index 85 mutant.
    other = _write(workspace / "eq80.csv", HEADER + EQUIVALENT_85.replace(",85,", ",80,"))
    assert merge_mutations(pit, CUSTOM, other)["equivalent"] == 1
    assert rescore({**merge_mutations(pit, CUSTOM), "alive": alive_mutants(pit, CUSTOM)},
                   load_equivalents(other))["equivalent"] == 0


def test_empty_index_and_old_header_match_every_index(workspace):
    pit = list(read_pit_mutants(_write(workspace / "mutations.xml", PIT_XML)))
    any_index = _write(workspace / "any.csv", HEADER + EQUIVALENT_85.replace(",85,", ",,"))
    assert merge_mutations(pit, equivalents=any_index)["equivalent"] == 2
    old = _write(workspace / "old.csv", "source,class,method,line,mutator,description,reason\n"
                 f"pit,shop.PricingService,calculateDiscount,6,{BOUNDARY},changed conditional boundary,old\n")
    assert merge_mutations(pit, equivalents=old)["equivalent"] == 2
    bad = _write(workspace / "bad.csv", "source,class,line\n")
    with pytest.raises(ValueError, match="missing columns"):
        merge_mutations(pit, equivalents=bad)


def test_custom_index_is_stable_id(workspace):
    row = (f"custom,shop.PricingService,calculateDiscount,6,conditional,{CUSTOM[0]['id']},"
           "&& -> || (column 20),fixture\n")
    csv_path = _write(workspace / "eq.csv", HEADER + row)
    assert merge_mutations([], CUSTOM, csv_path)["equivalent"] == 1
    wrong = _write(workspace / "wrong.csv", HEADER + row.replace(":6:20:", ":6:21:"))
    assert merge_mutations([], CUSTOM, wrong)["equivalent"] == 0


def test_blocks_disambiguate_identical_indexes(workspace):
    xml = ("<mutations>" + _mutation("SURVIVED", 6, BOUNDARY, [85], [23])
           + _mutation("KILLED", 6, BOUNDARY, [85], [24]) + _mutation("KILLED", 9, MATH, [85], [30], "x")
           + "</mutations>")
    pit = list(read_pit_mutants(_write(workspace / "mutations.xml", xml)))
    assert [m["index"] for m in pit] == ["85@23", "85@24", "85"]
    same = ("<mutations>" + _mutation("SURVIVED", 6, BOUNDARY, [85], [23]) * 2 + "</mutations>")
    with pytest.raises(ValueError, match="not distinguishable"):
        list(read_pit_mutants(_write(workspace / "same.xml", same)))


def test_alive_list_validates_and_rescore_matches_merge(workspace):
    pit, custom = read_suite_results(_suite_results(workspace / "run-01"))
    alive = alive_mutants(pit, custom)
    assert [(a["source"], a["index"], a["status"], a["operator"]) for a in alive] == [
        ("pit", "85", "SURVIVED", "relational"),
        ("pit", "88", "NO_COVERAGE", "arithmetic"),
        ("custom", CUSTOM[0]["id"], "SURVIVED", "conditional"),
    ]
    stored = {**merge_mutations(pit, custom), "alive": alive}
    Draft202012Validator(SCHEMA["$defs"]["mutation"] | {"$defs": SCHEMA["$defs"]}).validate(stored)

    csv_path = _write(workspace / "eq.csv", HEADER + EQUIVALENT_85)
    expected = merge_mutations(pit, custom, csv_path)
    rescored = rescore(stored, load_equivalents(csv_path))
    for key in ("total", "killed", "equivalent", "score", "by_operator", "status_counts"):
        assert rescored[key] == expected[key], key
    assert rescore(stored, [])["score"] == pytest.approx(stored["score"])
    with pytest.raises(ValueError):
        rescore({**stored, "total": 1}, [])


def _raw_record(mutation):
    record = {
        "method": "p1-manual", "run": 1,
        "tests": {"generated": 3, "compiled": 3, "valid": 3, "passed": 3, "failed": 0, "skipped": 0,
                  "dropped": [], "compile_rate": 1.0},
        "coverage": {name: {"covered": 1, "total": 1, "pct": 100.0} for name in ("statement", "branch", "function")},
        "uncovered": {"lines": [], "branches": []},
        "mutation": mutation,
        "time": {"generation_s": 0.0, "execution_s": 1.0},
        "llm": {"calls": 0, "input_tokens": 0, "output_tokens": 0, "cost_usd": 0.0, "model": "", "effort": ""},
    }
    Draft202012Validator(SCHEMA).validate(record)
    return record


def _summary_score(out_dir):
    with (out_dir / "summary.csv").open(encoding="utf-8", newline="") as stream:
        rows = {(r["method"], r["metric"]): r for r in csv.DictReader(stream)}
    return float(rows[("p1-manual", "mutation_score")]["mean"])


@pytest.mark.parametrize("stored_alive", [False, True])
def test_analyze_rescores_with_current_csv_without_rewriting_raw(workspace, stored_alive):
    mutation_dir = workspace / "results/mutation"
    pit, custom = read_suite_results(_suite_results(mutation_dir / "p1-manual/run-01"))
    mutation = merge_mutations(pit, custom)
    if stored_alive:
        mutation["alive"] = alive_mutants(pit, custom)
        shutil.rmtree(mutation_dir / "p1-manual")  # stored list needs no saved reports
    raw = _write(workspace / "results/comparison/raw/p1-manual/run-01.json",
                 json.dumps(_raw_record(mutation), indent=2) + "\n")
    before = raw.read_bytes()
    _write(mutation_dir / "equivalent-mutants.csv", HEADER + EQUIVALENT_85)
    out_dir = workspace / "out"

    assert analyze.main(["--raw-dir", str(raw.parent.parent), "--out-dir", str(out_dir)]) == 0
    assert raw.read_bytes() == before
    assert mutation["score"] == pytest.approx(50.0)
    assert _summary_score(out_dir) == pytest.approx(60.0)
    summary = (out_dir / "summary.md").read_text(encoding="utf-8")
    assert "Equivalent mutants excluded in total: 1 across 1 recomputed runs" in summary
    origin = "stored alive list" if stored_alive else "backfilled from saved reports"
    assert f"| `p1-manual` | `run-01` | 6 | 1 | 0 | 50.0000 | 60.0000 | {origin} |" in summary


def test_analyze_keeps_stored_score_when_reports_missing_or_mismatched(workspace):
    mutation_dir = workspace / "results/mutation"
    pit, custom = read_suite_results(_suite_results(mutation_dir / "p1-manual/run-01"))
    mutation = merge_mutations(pit, custom[:1])  # saved reports differ from stored counts
    raw_dir = workspace / "results/comparison/raw"
    _write(raw_dir / "p1-manual/run-01.json", json.dumps(_raw_record(mutation)) + "\n")
    _write(mutation_dir / "equivalent-mutants.csv", HEADER + EQUIVALENT_85)
    out_dir = workspace / "out"
    analyze.analyze(raw_dir=raw_dir, out_dir=out_dir)
    assert _summary_score(out_dir) == pytest.approx(mutation["score"])
    summary = (out_dir / "summary.md").read_text(encoding="utf-8")
    assert "not recomputed" in summary and "saved reports do not match stored counts" in summary

    shutil.rmtree(mutation_dir / "p1-manual")
    analyze.analyze(raw_dir=raw_dir, out_dir=out_dir)
    assert "no alive list and no `run-01/mutations.xml`" in (out_dir / "summary.md").read_text(encoding="utf-8")


def test_mutation_ids_prints_paste_ready_rows_with_source(workspace, capsys):
    results = _suite_results(workspace / "results/mutation/p1-manual/run-01")
    source_root = workspace / "subject"
    _write(source_root / "src/main/java/shop/PricingService.java", SOURCE)
    csv_path = _write(workspace / "eq.csv", HEADER + EQUIVALENT_85)

    assert mutation_ids.main(["--suite-results", str(results), "--source-root", str(source_root),
                              "--equivalents", str(csv_path)]) == 0
    out = capsys.readouterr().out
    assert "3 (NO_COVERAGE 1, SURVIVED 2)" in out
    assert "#     if (max > 0 && discount > max) {" in out
    assert "#     discount = max;" not in out  # killed custom deletion is not listed
    assert "#     long discount = subtotal / 10;" not in out
    rows = [row for row in csv.reader(io.StringIO(out)) if row and not row[0].startswith("#")]
    assert rows == [
        ["pit", "shop.PricingService", "calculateDiscount", "6", BOUNDARY, "85", "changed conditional boundary", ""],
        ["pit", "shop.PricingService", "calculateDiscount", "9", MATH, "88",
         "Replaced long subtraction with addition", ""],
        ["custom", "shop.PricingService", "calculateDiscount", "6", "conditional", CUSTOM[0]["id"],
         "&& -> || (column 20)", ""],
    ]
    assert out.count("[already equivalent in CSV]") == 1

    # A pasted row (plus a reason) excludes exactly that mutant.
    pasted = _write(workspace / "pasted.csv", HEADER + ",".join(rows[0][:-1]) + ",reviewed\n")
    pit, custom = read_suite_results(results)
    assert merge_mutations(pit, custom, pasted)["equivalent"] == 1

    assert mutation_ids.main(["--suite-results", str(results), "--source-root", str(source_root),
                              "--format", "csv"]) == 0
    table = list(csv.DictReader(io.StringIO(capsys.readouterr().out)))
    assert [row["code"] for row in table] == ["if (max > 0 && discount > max) {", "return discount + 0;",
                                              "if (max > 0 && discount > max) {"]
    assert table[0]["file"] == "src/main/java/shop/PricingService.java"

    assert mutation_ids.main(["--suite-results", str(workspace / "missing")]) == 1
