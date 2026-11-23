from pathlib import Path

import pytest

from tools.svv.jacoco import parse_jacoco
from tools.svv.pit import merge_mutations, parse_pit, read_pit_mutants
from tools.svv.surefire import parse_surefire

FIXTURES = Path(__file__).parent / "fixtures"


def test_jacoco_exact_package_and_source_locations():
    result = parse_jacoco(FIXTURES / "jacoco.xml")
    assert result["coverage"] == {
        "statement": {"covered": 2, "total": 4, "pct": 50},
        "branch": {"covered": 1, "total": 4, "pct": 25},
        "function": {"covered": 1, "total": 3, "pct": pytest.approx(100 / 3)},
    }
    assert result["uncovered"] == {
        "lines": [{"class": "shop.Cart", "method": "second", "line": 20},
                  {"class": "shop.Cart$Item", "method": "value", "line": 31}],
        "branches": [{"class": "shop.Cart", "method": "first", "line": 7, "missed": 1, "covered": 1},
                     {"class": "shop.Cart", "method": "second", "line": 20, "missed": 2, "covered": 0}],
    }


def test_surefire_combines_suites_and_names_failed_tests():
    assert parse_surefire(FIXTURES) == {
        "tests": 4, "failures": 1, "errors": 1, "skipped": 1,
        "failed_tests": ["shop.OneTest.bad[1]", "shop.TwoTest.exception"],
    }


def test_pit_statuses_operators_and_score():
    result = parse_pit(FIXTURES / "mutations.xml", FIXTURES / "equivalent.csv")
    assert result == {
        "total": 6, "killed": 3, "survived": 1, "no_coverage": 1, "timed_out": 1,
        "equivalent": 1, "score": 60,
        "status_counts": dict.fromkeys(
            ["KILLED", "SURVIVED", "NO_COVERAGE", "TIMED_OUT", "MEMORY_ERROR", "RUN_ERROR"], 1),
        "by_operator": {
            "relational": {"source": "pit", "total": 1, "killed": 1},
            "arithmetic": {"source": "pit", "total": 2, "killed": 0},
            "conditional": {"source": "custom", "total": 0, "killed": 0},
            "boolean_constant": {"source": "custom", "total": 0, "killed": 0},
            "statement_deletion": {"source": "pit", "total": 2, "killed": 2},
            "other": {"source": "pit", "total": 1, "killed": 0},
        },
    }


def test_merge_scores_individual_mutants_from_both_sources():
    base = {"class": "shop.Cart", "method": "add", "line": 9, "status": "KILLED"}
    custom = [{**base, "mutator": "boolean_constant", "description": "change literal"},
              {**base, "mutator": "conditional", "description": "switch connective"},
              {**base, "mutator": "statement_deletion", "description": "remove assignment"},
              {**base, "mutator": "conditional", "description": "invalid", "status": "COMPILE_ERROR"}]
    result = merge_mutations(read_pit_mutants(FIXTURES / "mutations.xml"), custom,
                             FIXTURES / "equivalent.csv")
    assert (result["total"], result["killed"], result["equivalent"], result["score"]) == (
        9, 5, 2, pytest.approx(500 / 7))
    assert result["by_operator"]["statement_deletion"] == {"source": ["pit", "custom"], "total": 3, "killed": 3}
    assert result["by_operator"]["boolean_constant"]["killed"] == 0
    assert result["status_counts"]["COMPILE_ERROR"] == 1
    assert merge_mutations([], custom[:1], FIXTURES / "equivalent.csv")["score"] == 0
