import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
from uuid import uuid4

import pytest

from experiments.scripts import measure
from tools import generate_p2
from tools.svv.maven import MavenResult


@pytest.fixture
def workspace():
    path = generate_p2.ROOT / ".work" / f"test-p2-{uuid4().hex}"
    path.mkdir(parents=True)
    try:
        yield path
    finally:
        shutil.rmtree(path)


def _write_config(directory: Path, seed_base=1000, num_tests=16):
    cfg = directory / "config"
    cfg.mkdir(exist_ok=True)
    (cfg / "default.yaml").write_text(
        f"random:\n  seed_base: {seed_base}\n  num_tests: {num_tests}\n",
        encoding="utf-8",
    )
    return cfg


@pytest.mark.parametrize("bad_run", [0, -1, True])
def test_generate_rejects_invalid_run(workspace, bad_run):
    cfg = _write_config(workspace)
    with pytest.raises(ValueError, match="run"):
        generate_p2.generate(bad_run, config_dir=cfg, out_dir=workspace / "out", build_tools=False)


@pytest.mark.parametrize(
    "seed_base,num_tests,pattern",
    [
        (1000, 0, "num_tests"),
        (1000, -5, "num_tests"),
        (1000, "true", "num_tests"),
        ("true", 10, "seed_base"),
    ],
)
def test_generate_rejects_invalid_config(workspace, seed_base, num_tests, pattern):
    cfg = _write_config(workspace, seed_base=seed_base, num_tests=num_tests)
    with pytest.raises(ValueError, match=pattern):
        generate_p2.generate(1, config_dir=cfg, out_dir=workspace / "out", build_tools=False)


def test_build_raises_on_maven_failure(workspace, monkeypatch):
    monkeypatch.setattr(generate_p2, "run_maven", lambda cwd, goals: MavenResult(1, "out", "err", 0.1))
    with pytest.raises(RuntimeError, match="outerr"):
        generate_p2.build(workspace, "compile")


def test_generate_fails_cleanly_when_java_exits_nonzero(workspace, monkeypatch):
    cfg = _write_config(workspace, seed_base=1000, num_tests=8)

    def fake_run(*args, **kwargs):
        return subprocess.CompletedProcess(args[0], 1, stdout="", stderr="boom")

    monkeypatch.setattr(generate_p2.subprocess, "run", fake_run)
    with pytest.raises(RuntimeError, match="boom"):
        generate_p2.generate(1, config_dir=cfg, out_dir=workspace / "out", build_tools=False)


def test_end_to_end_determinism_metadata_and_suite_invariants(workspace):
    cfg = _write_config(workspace, seed_base=2000, num_tests=24)
    out_a = workspace / "run-01-a"
    out_b = workspace / "run-01-b"
    out_2 = workspace / "run-02"

    rec_a = generate_p2.generate(1, config_dir=cfg, out_dir=out_a, build_tools=True)
    rec_a_replay = generate_p2.generate(1, config_dir=cfg, out_dir=out_a, build_tools=False)
    rec_b = generate_p2.generate(1, config_dir=cfg, out_dir=out_b, build_tools=False)
    rec_2 = generate_p2.generate(2, config_dir=cfg, out_dir=out_2, build_tools=False)

    java_a = (out_a / "RandomShopTest.java").read_bytes()
    java_b = (out_b / "RandomShopTest.java").read_bytes()
    java_2 = (out_2 / "RandomShopTest.java").read_bytes()

    # Same seed -> byte-for-byte identical Java test suite.
    assert java_a == java_b
    assert hashlib.sha256(java_a).hexdigest() == rec_a["sha256"] == rec_b["sha256"]
    # Replay on same output directory preserves generation.json timestamps.
    assert rec_a_replay == rec_a
    # Different seed -> different suite.
    assert java_a != java_2
    assert rec_a["seed"] == 2001
    assert rec_2["seed"] == 2002

    # Suite format invariants: UTF-8 without BOM, package shop, class *Test, exact @Test count, no Random.
    assert not java_a.startswith(b"\xef\xbb\xbf")
    text = java_a.decode("utf-8")
    assert text.startswith("package shop;\n")
    assert re.search(r"\bclass\s+\w+Test\s*\{", text)
    assert len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE)) == 24
    assert "java.util.Random" not in text
    assert "new Random(" not in text
    assert "assertEquals(" in text
    assert "assertThrows(" in text

    # Metadata contract & compatibility with measure.py.
    meta = json.loads((out_a / "generation.json").read_text(encoding="utf-8"))
    assert meta["method"] == "p2-random"
    assert meta["run"] == 1
    assert meta["seed"] == 2001
    assert meta["tests"] == {"generated": 24}
    assert meta["generation_s"] >= 0
    assert meta["iterations"] == []
    assert meta["llm"] == {"calls": 0, "input_tokens": 0, "output_tokens": 0, "model": "", "effort": ""}
    assert measure.read_generation(out_a, "p2-random", 1) == meta
    llm = measure.llm_metrics(meta, {"model": "x", "effort": "y",
                                     "price_per_mtok_input": None, "price_per_mtok_output": None})
    assert llm["calls"] == 0
    assert llm["cost_usd"] == 0.0
    tests = measure.test_metrics(meta, {"tests": 24, "skipped": 0}, 24, 0)
    assert tests["generated"] == 24
    assert tests["compile_rate"] == 1.0


def test_generator_does_not_reference_subject():
    for rel in (
        "tools/generate_p2.py",
        "tools/random-gen/pom.xml",
        "tools/random-gen/src/main/java/p2/Generator.java",
    ):
        content = (generate_p2.ROOT / rel).read_text(encoding="utf-8")
        assert "subject" not in content.lower(), f"Unexpected reference to subject in {rel}"
