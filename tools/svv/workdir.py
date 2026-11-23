import shutil
from collections.abc import Iterator
from contextlib import contextmanager
from pathlib import Path
from uuid import uuid4

from .config import ROOT


@contextmanager
def workdir(suite: Path, program: str = "subject", keep: bool = False,
            root: Path = ROOT) -> Iterator[Path]:
    """Yield an isolated Maven project. With keep=True the caller owns cleanup."""
    if program not in ("subject", "reference"):
        raise ValueError(f"Unknown program: {program}")
    project = root / program
    if not (project / "pom.xml").is_file():
        raise FileNotFoundError(f"Missing Maven project: {project}")
    files = sorted(suite.rglob("*.java"))
    if not files:
        raise ValueError(f"No Java sources in suite: {suite}")
    parent = root / ".work"
    parent.mkdir(exist_ok=True)
    work = parent / f"{program}-{uuid4().hex}"
    work.mkdir()
    try:
        shutil.copytree(project, work, dirs_exist_ok=True,
                        ignore=shutil.ignore_patterns("target"))
        dest = work / "src/test/java/shop"
        dest.mkdir(parents=True, exist_ok=True)
        for source in files:
            target = dest / source.name
            if target.exists():
                raise ValueError(f"Duplicate test source: {source.name}")
            shutil.copy2(source, target)
        yield work
    finally:
        if not keep:
            shutil.rmtree(work)
