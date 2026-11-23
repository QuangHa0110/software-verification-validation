import os
import subprocess
from collections.abc import Sequence
from dataclasses import dataclass
from pathlib import Path
from time import perf_counter


@dataclass(frozen=True)
class MavenResult:
    returncode: int
    stdout: str
    stderr: str
    elapsed_s: float


def run_maven(cwd: Path, args: Sequence[str]) -> MavenResult:
    command = ["mvn.cmd" if os.name == "nt" else "mvn", "-B", *args]
    start = perf_counter()
    result = subprocess.run(command, cwd=cwd, capture_output=True, text=True,
                            encoding="utf-8", errors="replace", shell=False)
    return MavenResult(result.returncode, result.stdout, result.stderr, perf_counter() - start)
