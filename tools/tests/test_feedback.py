from pathlib import Path

import pytest

from tools.svv.config import ROOT
from tools.svv.feedback import coverage_feedback, mutation_feedback, mutation_outcomes, suite_blocks
from tools.svv.jacoco import parse_jacoco
from tools.svv.pit import read_pit_mutants
from tools.svv.test_filter import _stage_dir

FIXTURES = Path(__file__).parent / "fixtures"


@pytest.fixture
def workspace():
    with _stage_dir(ROOT, "feedback-") as directory:
        yield directory


def test_group_uncovered_lines_and_partial_branches_with_numbered_context(workspace):
    (workspace / "Cart.java").write_text("\n".join(f"code {i}" for i in range(1, 40)), encoding="utf-8")
    uncovered = parse_jacoco(FIXTURES / "jacoco.xml")["uncovered"]
    uncovered["lines"].append({"class": "shop.Cart", "method": "second", "line": 21})
    text = coverage_feedback(uncovered, workspace)
    assert "shop.Cart.first\n" in text
    assert "5: code 5" in text and "7: code 7  // branches covered=1 missed=1" in text
    assert "shop.Cart.second\n" in text and "shop.Cart$Item.value\n" in text
    assert text.count("20: code 20") == 1
    assert "20: code 20  // uncovered line, branches covered=0 missed=2" in text
    assert "21: code 21  // uncovered line" in text
    assert "shop.test" not in text


def test_pit_and_custom_feedback_only_includes_real_unresolved_targets(workspace):
    pit = list(read_pit_mutants(FIXTURES / "mutations.xml"))
    row = {"class": "shop.Cart", "method": "add", "line": 7, "mutator": "conditional",
           "operator": "conditional", "id": "Cart.java:7:14:conditional", "description": "swap condition",
           "original": "&&", "mutated": "||", "status": "SURVIVED"}
    killed, live = mutation_outcomes(pit, [row], None)
    assert len(killed) == 3
    text = mutation_feedback(live, ROOT / "subject/src/main/java/shop")
    assert text.count("shop.Cart.add\n") == 1
    for target in ("replace sum", "replace product", "arithmetic", "conditional", "SURVIVED",
                   "NO_COVERAGE", "swap condition", "original: '&&' -> replacement: '||'"):
        assert target in text
    for excluded in ("change boundary", "replace literal", "remove call"):
        assert excluded not in text
    eq = workspace / "equivalent.csv"
    eq.write_text("source,class,method,line,mutator,index,description,reason\n"
                  "custom,shop.Cart,add,7,conditional,,swap condition,fixture\n", encoding="utf-8")
    _, live = mutation_outcomes(pit, [row], eq)
    assert len(live) == 2


def test_inherited_multi_method_names_exclude_helpers_and_nested_declarations(workspace):
    (workspace / "ManyTest.java").write_text('''class ManyTest {
        int value = 2;
        void helper() {}
        @Test void first() { String text = "@Test void fake() {}"; }
        @ParameterizedTest @ValueSource(ints = {1, 2}) void second(int x) { assertTrue(x > 0); }
    }''', encoding="utf-8")
    assert [b.method_name for b in suite_blocks(workspace)] == ["first", "second"]
