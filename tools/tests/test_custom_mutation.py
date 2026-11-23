import argparse
import json
from pathlib import Path
import shutil
import subprocess
from uuid import uuid4

from jsonschema import Draft202012Validator
import pytest

from experiments.scripts import measure
from tools import mutate_custom
from tools.svv import custom_mutation
from tools.svv.custom_mutation import OPERATORS, enumerate_mutants, run_custom
from tools.svv.maven import MavenResult

FIXTURES = Path(__file__).parent / "fixtures"
SAMPLE_PROJECT = FIXTURES / "custom_mutation"
SAMPLE_JAVA = SAMPLE_PROJECT / "src/main/java/shop/Sample.java"
SCHEMA = json.loads((measure.ROOT / "experiments/schema/run-result.schema.json").read_text(encoding="utf-8"))


@pytest.fixture
def workspace():
    path = measure.ROOT / ".work" / f"test-custom-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def test_enumerate_mutants_filters_strings_comments_fields_and_for_header():
    mutants = enumerate_mutants(SAMPLE_PROJECT)
    second = enumerate_mutants(SAMPLE_PROJECT)
    assert mutants == second
    assert [m.record()["id"] for m in mutants] == [m.record()["id"] for m in second]

    summary = [(m.method, m.line, m.operator, m.original, m.mutated) for m in mutants]
    assert summary == [
        ("<init>", 9, "statement_deletion", "count = 1;", ""),
        ("evaluate", 13, "boolean_constant", "true", "false"),
        ("evaluate", 22, "statement_deletion", "local = 1;", ""),
        ("evaluate", 23, "statement_deletion", "local += 2;", ""),
        ("evaluate", 24, "statement_deletion", "local++;", ""),
        ("evaluate", 25, "statement_deletion", "--local;", ""),
        ("evaluate", 32, "conditional", "&&", "||"),
        ("evaluate", 32, "conditional", "||", "&&"),
        ("evaluate", 32, "statement_deletion", "local = 5;", ";"),
        ("evaluate", 33, "statement_deletion", "local--;", ";"),
        ("evaluate", 35, "statement_deletion", "++local;", ""),
        ("evaluate", 41, "statement_deletion", "count = 7;", ""),
        ("evaluate", 46, "statement_deletion", "local = 11;", ";"),
        ("evaluate", 49, "statement_deletion", "local = 12;", ";"),
        ("evaluate", 52, "statement_deletion", "local = 13;", "{}"),
        ("evaluate", 53, "statement_deletion", "local = 14;", "{}"),
        ("evaluate", 58, "boolean_constant", "false", "true"),
    ]
    # Verify required record keys and stability.
    for mutant in mutants:
        record = mutant.record()
        for key in ("id", "file", "class", "method", "line", "operator", "mutator",
                    "original", "mutated", "description"):
            assert key in record
        assert record["class"] == "shop.Sample"
        assert record["file"] == "src/main/java/shop/Sample.java"


def test_apply_mutants_edits_only_target_occurrence_and_preserves_rest():
    with SAMPLE_JAVA.open(encoding="utf-8", newline="") as stream:
        source = stream.read()
    mutants = enumerate_mutants(SAMPLE_PROJECT)
    by_key = {(m.line, m.operator, m.original): m for m in mutants}

    # Conditional: only the targeted operator on line 32 changes; strings/comments untouched.
    cond_and = by_key[(32, "conditional", "&&")].apply(source)
    assert "if (a || b || flag) local = 5;" in cond_and
    assert 'String noise = "a && b || true false";' in cond_and
    assert "// true && false; local = 9;" in cond_and

    cond_or = by_key[(32, "conditional", "||")].apply(source)
    assert "if (a && b && flag) local = 5;" in cond_or

    # Boolean constant: only literal changes, comment untouched.
    bool_true = by_key[(13, "boolean_constant", "true")].apply(source)
    assert "boolean flag = false;" in bool_true
    assert "return false;" in bool_true
    assert "/* false || true; local++; */" in bool_true

    bool_false = by_key[(58, "boolean_constant", "false")].apply(source)
    assert "return true;" in bool_false
    assert "boolean flag = true;" in bool_false

    # Statement deletion: braced, unbraced control, and switch arrow rule.
    deleted_braced = by_key[(22, "statement_deletion", "local = 1;")].apply(source)
    assert "local = 1;" not in deleted_braced
    assert "local += 2;" in deleted_braced

    deleted_unbraced = by_key[(32, "statement_deletion", "local = 5;")].apply(source)
    assert "if (a && b || flag) ;" in deleted_unbraced

    deleted_arrow = by_key[(52, "statement_deletion", "local = 13;")].apply(source)
    assert "case 1 -> {}" in deleted_arrow

    # Applying against modified source fails cleanly.
    with pytest.raises(ValueError, match="Source changed"):
        by_key[(13, "boolean_constant", "true")].apply(bool_true)


def test_apply_preserves_crlf_line_endings(workspace):
    shop = workspace / "src/main/java/shop"
    shop.mkdir(parents=True)
    source = "package shop;\r\nclass C {\r\n    boolean f(boolean a, boolean b) {\r\n        return a && b;\r\n    }\r\n}\r\n"
    (shop / "C.java").write_bytes(source.encode("utf-8"))
    mutants = enumerate_mutants(workspace)
    assert len(mutants) == 1
    assert mutants[0].line == 4
    mutated = mutants[0].apply(source)
    assert mutated == source.replace("&&", "||")


def test_run_custom_handles_compile_error_no_coverage_and_test_statuses(workspace, monkeypatch):
    subject = workspace / "subject"
    (subject / "pom.xml").parent.mkdir(parents=True)
    (subject / "pom.xml").touch()
    shop = subject / "src/main/java/shop"
    shop.mkdir(parents=True)
    (shop / "Mini.java").write_text(
        "package shop;\n"
        "public class Mini {\n"
        "    public boolean f(boolean a, boolean b) {\n"
        "        boolean x = true;\n"       # line 4: COMPILE_ERROR
        "        boolean y = false;\n"      # line 5: NO_COVERAGE
        "        if (a && b) return x;\n"   # line 6: KILLED
        "        if (a || b) return y;\n"   # line 7: TIMED_OUT
        "        x = a;\n"                  # line 8: SURVIVED
        "        return x;\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )
    suite = workspace / "suite"
    suite.mkdir()
    (suite / "MiniTest.java").touch()

    baseline = workspace / "baseline"
    shutil.copytree(subject, baseline)
    jacoco = baseline / "target/site/jacoco/jacoco.xml"
    jacoco.parent.mkdir(parents=True)
    jacoco.write_text(
        '<report name="mini"><package name="shop"><sourcefile name="Mini.java">'
        '<line nr="4" mi="0" ci="1"/>'
        '<line nr="5" mi="1" ci="0"/>'
        '<line nr="6" mi="0" ci="2"/>'
        '<line nr="7" mi="0" ci="2"/>'
        '<line nr="8" mi="0" ci="2"/>'
        "</sourcefile></package></report>",
        encoding="utf-8",
    )

    executed_lines = []

    def fake_maven(project, args):
        assert args == ["-Djacoco.skip=true", "test-compile"]
        text = (project / "src/main/java/shop/Mini.java").read_text(encoding="utf-8")
        if "boolean x = false;" in text:
            return MavenResult(1, "[ERROR] COMPILATION ERROR : maven-compiler-plugin", "", 0.1)
        return MavenResult(0, "", "", 0.1)

    def fake_run_tests(project, timeout):
        assert timeout == pytest.approx(3 * 2.5 + 10)
        text = (project / "src/main/java/shop/Mini.java").read_text(encoding="utf-8")
        if "if (a || b) return x;" in text:
            executed_lines.append(6)
            return "KILLED"
        if "if (a && b) return y;" in text:
            executed_lines.append(7)
            return "TIMED_OUT"
        if "x = a;" not in text:
            executed_lines.append(8)
            return "SURVIVED"
        raise AssertionError(f"Unexpected mutant executed:\n{text}")

    monkeypatch.setattr(custom_mutation, "run_maven", fake_maven)
    monkeypatch.setattr(custom_mutation, "_run_tests", fake_run_tests)

    rows = run_custom(
        baseline, suite, 2.5,
        {"timeout_factor": 3, "custom_timeout_constant_s": 10, "custom_workers": 2},
        root=workspace,
    )
    assert [(r["line"], r["operator"], r["status"]) for r in rows] == [
        (4, "boolean_constant", "COMPILE_ERROR"),
        (5, "boolean_constant", "NO_COVERAGE"),
        (6, "conditional", "KILLED"),
        (7, "conditional", "TIMED_OUT"),
        (8, "statement_deletion", "SURVIVED"),
    ]
    assert "COMPILATION ERROR" in rows[0]["diagnostic"]
    assert sorted(executed_lines) == [6, 7, 8]
    assert not list((workspace / ".work").iterdir())


def test_run_tests_classifies_killed_survived_and_timeout(workspace, monkeypatch):
    class FakeProcess:
        def __init__(self, returncode=0, timeout=False):
            self.pid = 12345
            self.returncode = returncode
            self._timeout = timeout
            self._calls = 0

        def __enter__(self):
            return self

        def __exit__(self, *exc):
            return False

        def communicate(self, timeout=None):
            self._calls += 1
            if self._timeout and self._calls == 1:
                raise subprocess.TimeoutExpired(cmd=["mvn"], timeout=timeout)
            return ("mvn output", None)

    reports = workspace / "target/surefire-reports"
    reports.mkdir(parents=True)

    # SURVIVED
    (reports / "TEST-shop.ATest.xml").write_text('<testsuite tests="2" failures="0" errors="0" skipped="0"/>',
                                                 encoding="utf-8")
    monkeypatch.setattr(custom_mutation.subprocess, "Popen", lambda *a, **kw: FakeProcess(0))
    assert custom_mutation._run_tests(workspace, 5) == "SURVIVED"

    # KILLED
    (reports / "TEST-shop.ATest.xml").write_text('<testsuite tests="2" failures="1" errors="0" skipped="0"/>',
                                                 encoding="utf-8")
    monkeypatch.setattr(custom_mutation.subprocess, "Popen", lambda *a, **kw: FakeProcess(1))
    assert custom_mutation._run_tests(workspace, 5) == "KILLED"

    # TIMED_OUT
    killed_pids = []
    monkeypatch.setattr(custom_mutation.subprocess, "Popen", lambda *a, **kw: FakeProcess(1, timeout=True))
    monkeypatch.setattr(custom_mutation.subprocess, "run", lambda cmd, **kw: killed_pids.append(cmd))
    if hasattr(custom_mutation.os, "killpg"):
        monkeypatch.setattr(custom_mutation.os, "killpg", lambda pid, sig: killed_pids.append(pid))
    assert custom_mutation._run_tests(workspace, 5) == "TIMED_OUT"
    assert killed_pids


def test_measure_merges_pit_and_custom_mutants_and_writes_json(workspace, monkeypatch):
    (workspace / "subject").mkdir()
    (workspace / "subject/pom.xml").touch()
    suite = workspace / "suite"
    suite.mkdir()
    (suite / "SampleTest.java").touch()
    config_dir = workspace / "experiments/config"
    config_dir.mkdir(parents=True)
    shutil.copy2(measure.ROOT / "experiments/config/default.yaml", config_dir / "default.yaml")
    schema_dir = workspace / "experiments/schema"
    schema_dir.mkdir(parents=True)
    shutil.copy2(measure.ROOT / "experiments/schema/run-result.schema.json",
                 schema_dir / "run-result.schema.json")
    eq_dir = workspace / "results/mutation"
    eq_dir.mkdir(parents=True)
    shutil.copy2(FIXTURES / "equivalent.csv", eq_dir / "equivalent-mutants.csv")

    monkeypatch.setattr(measure, "ROOT", workspace)

    def fake_maven(path, args):
        if args == ["test"]:
            sf = path / "target/surefire-reports"
            sf.mkdir(parents=True)
            (sf / "TEST-shop.SampleTest.xml").write_text(
                '<testsuite tests="2" failures="0" errors="0" skipped="0"/>', encoding="utf-8")
            jc = path / "target/site/jacoco"
            jc.mkdir(parents=True)
            shutil.copy2(FIXTURES / "jacoco.xml", jc / "jacoco.xml")
            (jc / "index.html").touch()
            return MavenResult(0, "", "", 1.5)
        if any("mutationCoverage" in a for a in args):
            pit = path / "target/pit-reports"
            pit.mkdir(parents=True)
            shutil.copy2(FIXTURES / "mutations.xml", pit / "mutations.xml")
            (pit / "index.html").touch()
            return MavenResult(0, "", "", 2.0)
        raise AssertionError(f"Unexpected maven args: {args}")

    custom_rows = [
        {"id": "src/main/java/shop/Cart.java:9:5:boolean_constant", "file": "src/main/java/shop/Cart.java",
         "class": "shop.Cart", "method": "add", "line": 9, "operator": "boolean_constant",
         "mutator": "boolean_constant", "original": "true", "mutated": "false",
         "description": "change literal", "status": "KILLED"},
        {"id": "src/main/java/shop/Cart.java:10:5:conditional", "file": "src/main/java/shop/Cart.java",
         "class": "shop.Cart", "method": "add", "line": 10, "operator": "conditional",
         "mutator": "conditional", "original": "&&", "mutated": "||",
         "description": "&& -> || (column 5)", "status": "KILLED"},
        {"id": "src/main/java/shop/Cart.java:11:5:statement_deletion", "file": "src/main/java/shop/Cart.java",
         "class": "shop.Cart", "method": "add", "line": 11, "operator": "statement_deletion",
         "mutator": "statement_deletion", "original": "x = 1;", "mutated": "",
         "description": "delete x = 1; (column 5)", "status": "SURVIVED"},
        {"id": "src/main/java/shop/Cart.java:12:5:statement_deletion", "file": "src/main/java/shop/Cart.java",
         "class": "shop.Cart", "method": "add", "line": 12, "operator": "statement_deletion",
         "mutator": "statement_deletion", "original": "y = 1;", "mutated": "",
         "description": "delete y = 1; (column 5)", "status": "COMPILE_ERROR"},
    ]
    monkeypatch.setattr(measure, "run_maven", fake_maven)
    monkeypatch.setattr(measure, "run_custom", lambda *a, **kw: custom_rows)

    args = argparse.Namespace(method="smoke", run=1, suite=suite, keep_workdir=False,
                              program="subject", mutation=True)
    measure.measure(args)

    raw_path = workspace / "results/comparison/raw/smoke/run-01.json"
    custom_path = workspace / "results/mutation/smoke/run-01/custom-mutations.json"
    assert raw_path.is_file()
    assert custom_path.is_file()
    assert json.loads(custom_path.read_text(encoding="utf-8")) == custom_rows

    result = json.loads(raw_path.read_text(encoding="utf-8"))
    Draft202012Validator(SCHEMA).validate(result)
    mut = result["mutation"]
    assert mut["total"] == 9
    assert mut["equivalent"] == 2
    assert mut["killed"] == 4
    assert mut["status_counts"]["COMPILE_ERROR"] == 1
    assert mut["by_operator"]["statement_deletion"] == {"source": ["pit", "custom"], "total": 3, "killed": 2}
    assert mut["by_operator"]["conditional"] == {"source": "custom", "total": 1, "killed": 1}
    assert mut["by_operator"]["boolean_constant"] == {"source": "custom", "total": 1, "killed": 0}


def test_cli_list_outputs_operator_counts(capsys):
    exit_code = mutate_custom.main(["--list", "--project", str(SAMPLE_PROJECT)])
    assert exit_code == 0
    payload = json.loads(capsys.readouterr().out)
    assert set(payload["by_operator"]) == set(OPERATORS)
    assert payload["by_operator"] == {"conditional": 2, "boolean_constant": 2, "statement_deletion": 13}
    assert len(payload["mutants"]) == 17
