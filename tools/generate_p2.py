"""Generate one seeded P2 suite using the locally installed reference artifact."""

import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path
import subprocess
from tempfile import TemporaryDirectory
from time import perf_counter

try:
    from tools.svv.config import ROOT, load_config
    from tools.svv.maven import run_maven
except ModuleNotFoundError:
    from svv.config import ROOT, load_config
    from svv.maven import run_maven

REQUIRED_METADATA_KEYS = {"started_at", "ended_at", "generation_s", "iterations", "llm"}


def _is_int(value) -> bool:
    return isinstance(value, int) and not isinstance(value, bool)


def build(directory: Path, *goals: str) -> None:
    result = run_maven(directory, goals)
    if result.returncode:
        raise RuntimeError(result.stdout + result.stderr)


def _read_previous_metadata(path: Path) -> dict:
    if not path.exists():
        return {}
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {}
    return data if isinstance(data, dict) else {}


def generate(run: int, *, root: Path = ROOT, config_dir: Path | None = None,
             out_dir: Path | None = None, build_tools: bool = True) -> dict:
    cfg_dir = config_dir if config_dir is not None else root / "experiments/config"
    config = load_config(cfg_dir)["random"]
    seed_base = config["seed_base"]
    count = config["num_tests"]
    if not _is_int(run) or run < 1:
        raise ValueError("run must be a positive integer")
    if not _is_int(seed_base):
        raise ValueError("random.seed_base must be an integer")
    if not _is_int(count) or count < 1:
        raise ValueError("random.num_tests must be a positive integer")

    seed = seed_base + run
    reference = root / "reference"
    generator = root / "tools/random-gen"
    out = out_dir if out_dir is not None else root / "test-suites/p2-random" / f"run-{run:02d}"
    out.mkdir(parents=True, exist_ok=True)
    if build_tools:
        build(reference, "install", "-DskipTests")
        build(generator, "compile")

    classpath = os.pathsep.join(str(path / "target/classes") for path in (generator, reference))
    with TemporaryDirectory(dir=out.parent) as tmp:
        source = Path(tmp) / "RandomShopTest.java"
        started = datetime.now(timezone.utc)
        start = perf_counter()
        proc = subprocess.run(
            ["java", "-cp", classpath, "p2.Generator", str(seed), str(count), str(source)],
            capture_output=True, text=True, encoding="utf-8", errors="replace",
        )
        duration = perf_counter() - start
        ended = datetime.now(timezone.utc)
        if proc.returncode:
            raise RuntimeError(f"p2.Generator failed with exit {proc.returncode}:\n{proc.stdout}{proc.stderr}")

        payload = source.read_bytes()
        identity = {
            "method": "p2-random",
            "run": run,
            "seed": seed,
            "tests": {"generated": count},
            "sha256": hashlib.sha256(payload).hexdigest(),
        }
        metadata = out / "generation.json"
        target = out / source.name
        # Preserve the timing of the original identical suite on replay.
        previous = _read_previous_metadata(metadata)
        if (all(previous.get(k) == v for k, v in identity.items())
                and REQUIRED_METADATA_KEYS <= previous.keys()
                and target.exists() and target.read_bytes() == payload):
            record = previous
        else:
            record = dict(
                identity,
                started_at=started.isoformat(),
                ended_at=ended.isoformat(),
                generation_s=duration,
                iterations=[],
                llm={"calls": 0, "input_tokens": 0, "output_tokens": 0, "model": "", "effort": ""},
            )
            staged = Path(tmp) / metadata.name
            staged.write_text(json.dumps(record, indent=2) + "\n", encoding="utf-8", newline="\n")
            source.replace(target)
            staged.replace(metadata)

    print(f"{out.name}: seed={seed} tests={count} sha256={identity['sha256']}")
    return record


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run", type=int, required=True)
    generate(parser.parse_args().run)
