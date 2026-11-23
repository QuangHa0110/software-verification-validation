"""Filter LLM-generated JUnit 5 test blocks against subject/ and reference/."""

import re
import shutil
import xml.etree.ElementTree as ET
from collections.abc import Iterator, Sequence
from contextlib import contextmanager
from dataclasses import dataclass
from pathlib import Path
from uuid import uuid4

from .config import ROOT
from .maven import run_maven
from .test_blocks import TestBlock, block_signature, class_name, extract_block_from_java, wrap_block
from .workdir import workdir

REASON_COMPILE_ERROR = "compile_error"
REASON_FAILS_ON_REFERENCE = "fails_on_reference"
REASON_FAILS_ON_SUBJECT_ONLY = "fails_on_subject_only"
REASON_DUPLICATE = "duplicate"


@dataclass(frozen=True)
class FilteredBlock:
    block: TestBlock
    class_name: str
    source: str


@dataclass(frozen=True)
class DroppedTest:
    name: str
    reason: str
    class_name: str = ""
    detail: str = ""


@dataclass(frozen=True)
class FilterResult:
    kept: list[FilteredBlock]
    dropped: list[DroppedTest]
    generated: int
    compiled: int
    passed_reference: int
    valid: int
    compile_rate: float | None
    oracle_rate: float | None

    def to_dict(self) -> dict:
        return {
            "generated": self.generated,
            "compiled": self.compiled,
            "passed_reference": self.passed_reference,
            "valid": self.valid,
            "compile_rate": self.compile_rate,
            "oracle_rate": self.oracle_rate,
            "dropped": [{"name": item.name, "reason": item.reason} for item in self.dropped],
        }


def load_blocks(source: Sequence[TestBlock] | Path) -> list[TestBlock]:
    """Load TestBlocks from an iterable of TestBlocks or a directory of .java files."""
    if isinstance(source, Path):
        blocks: list[TestBlock] = []
        if not source.exists():
            return blocks
        for path in sorted(source.rglob("*.java")):
            block = extract_block_from_java(path.read_text(encoding="utf-8"))
            if block is not None:
                blocks.append(block)
        return blocks
    return list(source)


@contextmanager
def _stage_dir(root: Path, prefix: str) -> Iterator[Path]:
    parent = root / ".work"
    parent.mkdir(parents=True, exist_ok=True)
    stage = parent / f"{prefix}{uuid4().hex}"
    stage.mkdir(parents=False, exist_ok=False)
    try:
        yield stage
    finally:
        shutil.rmtree(stage, ignore_errors=True)


def _stage_sources(items: Sequence[FilteredBlock], directory: Path) -> None:
    directory.mkdir(parents=True, exist_ok=True)
    for item in items:
        (directory / f"{item.class_name}.java").write_text(item.source, encoding="utf-8")


def _extract_failed_filenames(output: str, active_files: set[str]) -> set[str]:
    """Identify candidate .java filenames mentioned in javac/Maven error output."""
    found: set[str] = set()
    for filename in active_files:
        pattern = re.compile(rf"(?:^|[/\\:\s\[]){re.escape(filename)}(?:[:\]]|\s|$)")
        if pattern.search(output):
            found.add(filename)
    return found


def _compile_batch(
    candidates: list[FilteredBlock],
    root: Path,
) -> tuple[list[FilteredBlock], list[DroppedTest]]:
    """Compile all candidates in one Maven workdir, iteratively dropping broken files."""
    if not candidates:
        return [], []

    active = list(candidates)
    dropped: list[DroppedTest] = []

    with _stage_dir(root, "stage-") as stage:
        _stage_sources(active, stage)
        with workdir(stage, program="subject", root=root) as work:
            test_dir = work / "src/test/java/shop"
            while active:
                res = run_maven(work, ["-Djacoco.skip=true", "test-compile"])
                if res.returncode == 0:
                    break
                combined = f"{res.stdout}\n{res.stderr}"
                by_file = {f"{item.class_name}.java": item for item in active}
                bad_files = _extract_failed_filenames(combined, set(by_file))

                if not bad_files:
                    if len(active) == 1:
                        bad_files = set(by_file)
                    else:
                        # Fallback: isolate broken files by bisecting into halves.
                        mid = len(active) // 2
                        left_ok, left_drop = _compile_batch(active[:mid], root)
                        right_ok, right_drop = _compile_batch(active[mid:], root)
                        return left_ok + right_ok, dropped + left_drop + right_drop

                next_active: list[FilteredBlock] = []
                for item in active:
                    fname = f"{item.class_name}.java"
                    if fname in bad_files:
                        dropped.append(
                            DroppedTest(
                                name=item.block.method_name,
                                reason=REASON_COMPILE_ERROR,
                                class_name=item.class_name,
                                detail=combined.strip()[:1000],
                            )
                        )
                        target = test_dir / fname
                        if target.exists():
                            target.unlink()
                    else:
                        next_active.append(item)
                test_classes = work / "target/test-classes"
                if test_classes.exists():
                    shutil.rmtree(test_classes)
                active = next_active

    return active, dropped


def _evaluate_surefire_per_class(reports_dir: Path, class_names: Sequence[str]) -> dict[str, tuple[bool, str]]:
    """Return {class_name: (passed, detail)} from Surefire XML reports."""
    outcomes: dict[str, tuple[bool, str]] = {}
    for cls in class_names:
        report_path = reports_dir / f"TEST-shop.{cls}.xml"
        if not report_path.is_file():
            outcomes[cls] = (False, f"Missing Surefire report for shop.{cls}")
            continue
        suite = ET.parse(report_path).getroot()
        tests = int(suite.get("tests", "0"))
        failures = int(suite.get("failures", "0"))
        errors = int(suite.get("errors", "0"))
        skipped = int(suite.get("skipped", "0"))
        if tests >= 1 and failures == 0 and errors == 0 and skipped == 0:
            outcomes[cls] = (True, "")
        else:
            details: list[str] = []
            for tc in suite.findall("testcase"):
                for tag in ("failure", "error", "skipped"):
                    node = tc.find(tag)
                    if node is not None:
                        first_line = next(iter((node.text or "").strip().splitlines()), "")
                        msg = node.get("message") or first_line
                        details.append(f"{tc.attrib.get('name')}: {tag} {msg}")
            summary = f"tests={tests} failures={failures} errors={errors} skipped={skipped}"
            outcomes[cls] = (False, "; ".join(details) if details else summary)
    return outcomes


def _run_suite_on_program(
    candidates: Sequence[FilteredBlock],
    program: str,
    root: Path,
) -> dict[str, tuple[bool, str]]:
    """Run all candidates once with Surefire on `program` and return per-class pass/fail."""
    if not candidates:
        return {}
    with _stage_dir(root, f"run-{program}-") as stage:
        _stage_sources(candidates, stage)
        with workdir(stage, program=program, root=root) as work:
            run_maven(work, ["-Djacoco.skip=true", "-Dmaven.test.failure.ignore=true", "test"])
            reports_dir = work / "target/surefire-reports"
            return _evaluate_surefire_per_class(reports_dir, [item.class_name for item in candidates])


def filter_tests(
    candidates: Sequence[TestBlock] | Path,
    *,
    existing: Sequence[TestBlock] | Path | None = None,
    class_prefix: str = "Llm",
    start_index: int = 1,
    root: Path = ROOT,
) -> FilterResult:
    """Filter test blocks according to design section 4.2 rules."""
    blocks = load_blocks(candidates)
    existing_blocks = load_blocks(existing) if existing is not None else []
    generated = len(blocks)

    wrapped = [
        FilteredBlock(
            block=block,
            class_name=(cls := class_name(class_prefix, start_index + idx)),
            source=wrap_block(block, cls),
        )
        for idx, block in enumerate(blocks)
    ]

    # Step 1: Compile batch against subject/
    compiled_items, dropped_compile = _compile_batch(wrapped, root=root)
    compiled_count = len(compiled_items)

    # Step 2: Run compiled tests on reference/ (oracle verification)
    ref_outcomes = _run_suite_on_program(compiled_items, program="reference", root=root)
    passed_ref_items: list[FilteredBlock] = []
    dropped_ref: list[DroppedTest] = []
    for item in compiled_items:
        ok, detail = ref_outcomes.get(item.class_name, (False, "No result"))
        if ok:
            passed_ref_items.append(item)
        else:
            dropped_ref.append(
                DroppedTest(
                    name=item.block.method_name,
                    reason=REASON_FAILS_ON_REFERENCE,
                    class_name=item.class_name,
                    detail=detail,
                )
            )
    passed_reference_count = len(passed_ref_items)

    # Step 3: Run reference-passing tests on subject/
    subj_outcomes = _run_suite_on_program(passed_ref_items, program="subject", root=root)
    passed_both_items: list[FilteredBlock] = []
    dropped_subj: list[DroppedTest] = []
    for item in passed_ref_items:
        ok, detail = subj_outcomes.get(item.class_name, (False, "No result"))
        if ok:
            passed_both_items.append(item)
        else:
            dropped_subj.append(
                DroppedTest(
                    name=item.block.method_name,
                    reason=REASON_FAILS_ON_SUBJECT_ONLY,
                    class_name=item.class_name,
                    detail=detail,
                )
            )

    # Step 4: Deduplicate by normalized body against existing and earlier kept blocks
    seen_signatures = {block_signature(block) for block in existing_blocks}
    kept: list[FilteredBlock] = []
    dropped_dup: list[DroppedTest] = []
    for item in passed_both_items:
        sig = block_signature(item.block)
        if sig in seen_signatures:
            dropped_dup.append(
                DroppedTest(
                    name=item.block.method_name,
                    reason=REASON_DUPLICATE,
                    class_name=item.class_name,
                    detail="Duplicate normalized test body",
                )
            )
        else:
            seen_signatures.add(sig)
            kept.append(item)

    # Preserve original candidate order in dropped list for deterministic reporting
    dropped_by_class = {
        d.class_name: d for d in (*dropped_compile, *dropped_ref, *dropped_subj, *dropped_dup)
    }
    ordered_dropped = [dropped_by_class[item.class_name] for item in wrapped if item.class_name in dropped_by_class]

    compile_rate = compiled_count / generated if generated else None
    oracle_rate = passed_reference_count / compiled_count if compiled_count else None
    return FilterResult(
        kept=kept,
        dropped=ordered_dropped,
        generated=generated,
        compiled=compiled_count,
        passed_reference=passed_reference_count,
        valid=len(kept),
        compile_rate=compile_rate,
        oracle_rate=oracle_rate,
    )


def write_suite(
    kept: Sequence[FilteredBlock],
    *,
    method: str,
    run: int,
    root: Path = ROOT,
    output_dir: Path | None = None,
) -> Path:
    """Write kept FilteredBlocks as one .java file per block into test-suites/<method>/run-XX/."""
    if output_dir is None:
        if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9_-]*", method) or run < 1:
            raise ValueError("Expected a method identifier and a positive run number")
        target_dir = root / "test-suites" / method / f"run-{run:02d}"
    else:
        target_dir = output_dir

    target_dir.mkdir(parents=True, exist_ok=True)
    for item in kept:
        (target_dir / f"{item.class_name}.java").write_text(item.source, encoding="utf-8")
    return target_dir
