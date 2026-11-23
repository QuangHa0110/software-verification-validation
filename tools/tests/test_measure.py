import argparse
import json
from pathlib import Path
import shutil
from uuid import uuid4

from jsonschema import Draft202012Validator
import pytest

from experiments.scripts import measure
from tools.svv.config import load_config
from tools.svv.jacoco import parse_jacoco
from tools.svv.maven import MavenResult
from tools.svv.pit import parse_pit
from tools.svv.workdir import workdir

FIXTURES = Path(__file__).parent / "fixtures"
SCHEMA = json.loads((measure.ROOT / "experiments/schema/run-result.schema.json").read_text(encoding="utf-8"))


@pytest.fixture
def workspace():
    path = measure.ROOT / ".work" / f"test-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def test_config_merges_nested_overrides_in_order(workspace):
    (workspace / "default.yaml").write_text("nested: {first: 1, last: 2}\nops: [a, b]", encoding="utf-8")
    (workspace / "z.local.yaml").write_text("nested: {last: 4}", encoding="utf-8")
    (workspace / "a.local.yaml").write_text("nested: {last: 3}\nops: [c]", encoding="utf-8")
    assert load_config(workspace) == {"nested": {"first": 1, "last": 4}, "ops": ["c"]}


@pytest.mark.parametrize("keep", [False, True])
def test_workdir_uses_requested_program_and_excludes_target(workspace, keep):
    project = workspace / "reference"
    (project / "target").mkdir(parents=True)
    (project / "pom.xml").touch()
    suite = workspace / "suite"
    suite.mkdir()
    (suite / "OneTest.java").touch()
    with workdir(suite, program="reference", root=workspace, keep=keep) as path:
        assert (path / "pom.xml").is_file()
        assert (path / "src/test/java/shop/OneTest.java").is_file()
        assert not (path / "target").exists()
    assert path.exists() == keep
    assert not (project / "src").exists()


def test_sample_run_validates_and_invalid_counter_is_rejected():
    Draft202012Validator.check_schema(SCHEMA)
    validator = Draft202012Validator(SCHEMA)
    sample = {"method": "smoke", "run": 1,
              "tests": {"generated": 2, "compiled": 2, "valid": 2, "passed": 2, "failed": 0, "dropped": []},
              **parse_jacoco(FIXTURES / "jacoco.xml"), "mutation": parse_pit(FIXTURES / "mutations.xml"),
              "time": {"generation_s": 0, "execution_s": 1},
              "llm": {"calls": 0, "input_tokens": 0, "output_tokens": 0, "cost_usd": 0, "model": "", "effort": ""}}
    validator.validate(sample)
    sample["coverage"]["statement"]["covered"] = -1
    assert not validator.is_valid(sample)


def test_repeat_publication_replaces_reports_only_after_success(workspace):
    results = workspace / "results"
    coverage = workspace / "jacoco"
    coverage.mkdir()
    (coverage / "index.html").touch()
    pit = workspace / "pit"
    pit.mkdir()
    (pit / "index.html").touch()

    def publish(run_id, mutation):
        work = workspace / run_id
        work.mkdir()
        return measure.publish(work, results, "smoke", "run-01", {"id": run_id}, coverage, mutation)

    raw = publish("first", pit)
    archive = results / "mutation/smoke/run-01"
    assert (archive / "index.html").is_file()
    (pit / "index.html").unlink()
    with pytest.raises(FileNotFoundError):
        publish("incomplete", pit)
    assert json.loads(raw.read_text(encoding="utf-8")) == {"id": "first"}
    assert (archive / "index.html").is_file()
    publish("coverage-only", None)
    assert json.loads(raw.read_text(encoding="utf-8")) == {"id": "coverage-only"}
    assert not archive.exists()


def test_generation_metrics_and_p4_metadata(workspace):
    generation = {"generation_s": 8, "method": "p4-coverage-guided", "run": 1,
                  "llm": {"model": "used-model", "calls": 1, "input_tokens": 20, "output_tokens": 10},
                  "tests": {"generated": 5, "compiled": 4, "passed_reference": 3, "valid": 2},
                  "iterations": []}
    (workspace / "generation.json").write_text(json.dumps(generation), encoding="utf-8")
    assert measure.read_generation(workspace, generation["method"], 1) == generation
    with pytest.raises(ValueError, match="expected metadata"):
        measure.read_generation(workspace, generation["method"], 2)
    config = load_config()["llm"] | {"price_per_mtok_input": 1, "price_per_mtok_output": 3}
    llm = measure.llm_metrics(generation, config)
    assert llm["cost_usd"] == 50 / 1e6
    assert llm["model"] == "used-model"
    assert measure.llm_metrics(generation, config | {"price_per_mtok_input": None})["cost_usd"] is None
    tests = measure.test_metrics(generation, {"tests": 2, "skipped": 0}, 2, 0)
    assert tests["compile_rate"] == 4 / 5
    assert tests["oracle_rate"] == 3 / 4


@pytest.mark.parametrize("failed", [0, 1])
def test_reference_only_runs_tests_and_reports_failures(workspace, monkeypatch, capsys, failed):
    (workspace / "reference").mkdir()
    (workspace / "reference/pom.xml").touch()
    suite = workspace / "suite"
    suite.mkdir()
    (suite / "SampleTest.java").touch()
    monkeypatch.setattr(measure, "ROOT", workspace)

    def fake_maven(path, args):
        assert args == ["-Djacoco.skip=true", "test"]
        reports = path / "target/surefire-reports"
        reports.mkdir(parents=True)
        xml = f'<testsuite tests="1" failures="{failed}" errors="0" skipped="0"/>'
        (reports / "TEST-shop.SampleTest.xml").write_text(xml, encoding="utf-8")
        return MavenResult(failed, "", "", 1.0)

    monkeypatch.setattr(measure, "run_maven", fake_maven)
    args = argparse.Namespace(method="smoke", run=1, suite=suite, keep_workdir=False, program="reference",
                              mutation=True)
    if failed:
        with pytest.raises(ValueError, match="Maven test exit 1"):
            measure.measure(args)
    else:
        measure.measure(args)
    assert f"passed={1-failed} failed={failed}" in capsys.readouterr().out
    assert not (workspace / "results").exists()
    assert not list((workspace / ".work").iterdir())
