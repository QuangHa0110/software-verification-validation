from pathlib import Path

import pytest

from tools.svv import test_filter
from tools.svv.maven import MavenResult
from tools.svv.test_blocks import TestBlock
from tools.svv.test_filter import (
    REASON_COMPILE_ERROR,
    REASON_DUPLICATE,
    REASON_FAILS_ON_REFERENCE,
    REASON_FAILS_ON_SUBJECT_ONLY,
    filter_tests,
    write_suite,
)


def _setup_fake_repo(root: Path) -> None:
    for program in ("subject", "reference"):
        (root / program).mkdir(parents=True)
        (root / program / "pom.xml").write_text("<project/>", encoding="utf-8")


def _write_surefire(reports_dir: Path, cls: str, *, tests: int = 1, failures: int = 0, errors: int = 0) -> None:
    reports_dir.mkdir(parents=True, exist_ok=True)
    body = ""
    if failures:
        body = '<testcase classname="shop.' + cls + '" name="m"><failure message="expected 1"/></testcase>'
    elif errors:
        body = '<testcase classname="shop.' + cls + '" name="m"><error message="boom"/></testcase>'
    else:
        body = '<testcase classname="shop.' + cls + '" name="m"/>'
    xml = f'<testsuite name="shop.{cls}" tests="{tests}" failures="{failures}" errors="{errors}" skipped="0">{body}</testsuite>'
    (reports_dir / f"TEST-shop.{cls}.xml").write_text(xml, encoding="utf-8")


def test_filter_tests_all_four_drop_reasons_with_mock_maven(tmp_path, monkeypatch):
    _setup_fake_repo(tmp_path)
    blocks = [
        TestBlock("badCompile", "@Test\nvoid badCompile() { COMPILE_ERR; }"),
        TestBlock("badOracle", "@Test\nvoid badOracle() { FAIL_REF; }"),
        TestBlock("subjectOnlyFail", "@Test\nvoid subjectOnlyFail() { FAIL_SUBJ; }"),
        TestBlock("goodOne", "@Test\nvoid goodOne() { assertEquals(2, 1 + 1); }"),
        TestBlock("dupOfGood", "@Test\nvoid dupOfGood() { /* same */ assertEquals(2,   1 + 1); }"),
        TestBlock("dupOfExisting", "@Test\nvoid dupOfExisting() { assertTrue(true); }"),
    ]
    existing = [TestBlock("alreadyKept", "@Test\nvoid alreadyKept() { assertTrue(true); }")]

    def fake_maven(work: Path, args):
        test_files = sorted((work / "src/test/java/shop").glob("*.java"))
        if "test-compile" in args:
            errors = []
            for path in test_files:
                if "COMPILE_ERR" in path.read_text(encoding="utf-8"):
                    errors.append(f"[ERROR] {path}:[12,5] cannot find symbol")
            if errors:
                return MavenResult(1, "\n".join(errors), "", 0.1)
            return MavenResult(0, "BUILD SUCCESS", "", 0.1)

        is_ref = work.name.startswith("reference-")
        reports = work / "target/surefire-reports"
        any_fail = False
        for path in test_files:
            src = path.read_text(encoding="utf-8")
            cls = path.stem
            if is_ref and "FAIL_REF" in src:
                _write_surefire(reports, cls, failures=1)
                any_fail = True
            elif (not is_ref) and "FAIL_SUBJ" in src:
                _write_surefire(reports, cls, errors=1)
                any_fail = True
            else:
                _write_surefire(reports, cls)
        return MavenResult(1 if any_fail else 0, "", "", 0.1)

    monkeypatch.setattr(test_filter, "run_maven", fake_maven)
    res = filter_tests(blocks, existing=existing, class_prefix="Mock", root=tmp_path)

    assert res.generated == 6
    assert res.compiled == 5
    assert res.passed_reference == 4
    assert res.valid == 1
    assert res.compile_rate == pytest.approx(5 / 6)
    assert res.oracle_rate == pytest.approx(4 / 5)
    assert [item.block.method_name for item in res.kept] == ["goodOne"]
    assert [(d.name, d.reason) for d in res.dropped] == [
        ("badCompile", REASON_COMPILE_ERROR),
        ("badOracle", REASON_FAILS_ON_REFERENCE),
        ("subjectOnlyFail", REASON_FAILS_ON_SUBJECT_ONLY),
        ("dupOfGood", REASON_DUPLICATE),
        ("dupOfExisting", REASON_DUPLICATE),
    ]

    metrics = res.to_dict()
    assert metrics["generated"] == 6
    assert metrics["valid"] == 1
    assert len(metrics["dropped"]) == 5

    suite_dir = write_suite(res.kept, method="p3-direct-llm", run=1, root=tmp_path)
    assert suite_dir == tmp_path / "test-suites/p3-direct-llm/run-01"
    written = list(suite_dir.glob("*.java"))
    assert len(written) == 1
    assert written[0].name == "Mock_004Test.java"


def test_filter_tests_empty_candidates(tmp_path):
    res = filter_tests([], root=tmp_path)
    assert res.generated == 0
    assert res.compiled == 0
    assert res.passed_reference == 0
    assert res.valid == 0
    assert res.compile_rate is None
    assert res.oracle_rate is None
    assert res.kept == []
    assert res.dropped == []


@pytest.mark.maven
def test_filter_tests_real_maven_with_four_intentional_blocks(tmp_path):
    blocks = [
        TestBlock(
            "compileErrorBlock",
            "@Test\nvoid compileErrorBlock() {\n    Cart c = new Cart();\n    c.nonExistentMethod();\n}",
        ),
        TestBlock(
            "wrongOracleBlock",
            "@Test\nvoid wrongOracleBlock() {\n    ShippingCalculator calc = new ShippingCalculator();\n    assertEquals(99999L, calc.calculate(Region.INNER_CITY, 500, 100_000L));\n}",
        ),
        TestBlock(
            "validShippingBlock",
            "@Test\nvoid validShippingBlock() {\n    ShippingCalculator calc = new ShippingCalculator();\n    assertEquals(15_000L, calc.calculate(Region.INNER_CITY, 500, 100_000L));\n}",
        ),
        TestBlock(
            "duplicateShippingBlock",
            "@Test\nvoid duplicateShippingBlock() {\n    // identical body with different formatting\n    ShippingCalculator calc = new ShippingCalculator();\n    assertEquals(15_000L,  calc.calculate(Region.INNER_CITY, 500, 100_000L));\n}",
        ),
    ]

    result = filter_tests(blocks, class_prefix="FilterCheck")

    assert result.generated == 4
    assert result.compiled == 3
    assert result.passed_reference == 2
    assert result.valid == 1
    assert result.compile_rate == pytest.approx(3 / 4)
    assert result.oracle_rate == pytest.approx(2 / 3)
    assert [k.block.method_name for k in result.kept] == ["validShippingBlock"]
    assert [(d.name, d.reason) for d in result.dropped] == [
        ("compileErrorBlock", REASON_COMPILE_ERROR),
        ("wrongOracleBlock", REASON_FAILS_ON_REFERENCE),
        ("duplicateShippingBlock", REASON_DUPLICATE),
    ]

    out_dir = write_suite(result.kept, method="filter-check", run=1, output_dir=tmp_path / "suite")
    saved = list(out_dir.glob("*.java"))
    assert len(saved) == 1
    assert saved[0].name == "FilterCheck_003Test.java"
    assert "package shop;" in saved[0].read_text(encoding="utf-8")
