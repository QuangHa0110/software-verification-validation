"""Kịch bản trình diễn 10 bước. Chạy từ repo bằng Python của môi trường svv."""

import argparse
import json
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT))
from tools import generate_p3, generate_p4
from tools.svv.jacoco import parse_jacoco
from tools.svv.maven import run_maven
from tools.svv.surefire import parse_surefire
from tools.svv.workdir import workdir

DEMO = ROOT / ".work/demo"
P3 = Path(generate_p3.METHOD) / "run-01"
P4 = Path(generate_p4.P4) / "run-01"
SMOKE = Path("test-suites/smoke")
SUMMARY = ROOT / "results/comparison/summary.md"
TITLES = (
    "Giới thiệu chương trình giỏ hàng và thanh toán",
    "Sinh test JUnit bằng LLM",
    "Chạy bộ P3 bằng JUnit",
    "Đọc báo cáo coverage ban đầu của P3",
    "Chuyển sang smoke và chỉ ra nhánh chưa phủ",
    "Đưa phản hồi coverage vào vòng lặp P4",
    "Chạy lại JUnit và so coverage với smoke",
    "Đọc kết quả mutation testing",
    "So sánh các phương pháp",
    "Hạn chế và đóng góp",
)


def junit(suite, label):
    with workdir(suite) as project:
        result = run_maven(project, ["clean", "test"])
        if result.returncode:
            raise RuntimeError(result.stdout + result.stderr)
        report = parse_surefire(project / "target/surefire-reports")
        print("JUnit", report)
        if report["tests"] <= report["skipped"] or report["errors"] or report["failures"]:
            raise ValueError("Bộ test chưa chạy thành công")
        destination = DEMO / "reports" / label
        shutil.copytree(project / "target/site/jacoco", destination, dirs_exist_ok=True)
        print(destination / "index.html")
        return parse_jacoco(destination / "jacoco.xml")


def show_coverage(report):
    for metric, value in report["coverage"].items():
        print(f"{metric}: {value['covered']}/{value['total']} ({value['pct']:.2f}%)")


def step(number, offline):
    archive = ROOT / "test-suites" / P3
    direct = archive if offline else DEMO / P3
    comparison = ROOT / "results/comparison"
    if number == 1:
        print((ROOT / "docs/spec/README.md").read_text(encoding="utf-8").split("##")[0])
    elif number == 2:
        if offline:
            log = ROOT / "results/llm-logs" / P3 / "calls.jsonl"
            with log.open(encoding="utf-8") as stream:
                call = json.loads(next(stream))
            print(f"Archive {log}: {call['purpose']}, {call['model']}")
            record = json.loads((archive / "generation.json").read_text(encoding="utf-8"))
        else:
            record = generate_p3.generate(1, out_dir=direct, complete_fn=generate_p4.demo_complete)
        print(direct, record["filter"])
        example = next(iter(sorted(direct.glob("*.java"))), None)
        if example is None:
            raise ValueError("P3 không có test Java để trình diễn")
        print(example.read_text(encoding="utf-8"))
    elif number == 3:
        junit(direct, "direct")
    elif number == 4:
        show_coverage(parse_jacoco(DEMO / "reports/direct/jacoco.xml"))
    elif number == 5:
        smoke = junit(ROOT / SMOKE, "smoke")
        show_coverage(smoke)
        branches = smoke["uncovered"]["branches"]
        print(generate_p4.coverage_feedback({"branches": branches[:3], "lines": []},
                                            ROOT / "subject/src/main/java/shop"))
    elif number == 6:
        cmd = [sys.executable, "tools/generate_p4.py", "--run", "1", "--demo", "--start-suite", SMOKE.as_posix()]
        if offline:
            cmd.append("--offline")
        print(subprocess.list2cmdline(cmd), flush=True)
        subprocess.run(cmd, cwd=ROOT, check=True)
    elif number == 7:
        after = junit(DEMO / P4, "after")
        print("Smoke ban đầu")
        show_coverage(parse_jacoco(DEMO / "reports/smoke/jacoco.xml"))
        print("Smoke + test P4 giữ lại")
        show_coverage(after)
    elif number == 8:
        path = comparison / "raw" / P3.with_suffix(".json")
        mutation = json.loads(path.read_text(encoding="utf-8"))["mutation"]
        print(path)
        print({k: mutation[k] for k in ("total", "killed", "equivalent", "score")})
        for operator, counts in mutation["by_operator"].items():
            print(operator, counts)
    elif number == 9:
        print(SUMMARY)
        for line in SUMMARY.read_text(encoding="utf-8").splitlines():
            if line.startswith("| Method | Metric") or "(`branch_pct`)" in line or "(`mutation_score`)" in line:
                print(line)
    else:
        print("P3 chạm trần trên module shop. Smoke minh họa cơ chế phản hồi. "
              "So sánh thực nghiệm dùng các run ghép cặp ở M5.")
        print("Đóng góp: oracle độc lập, bộ lọc test, phản hồi JaCoCo/PIT và dữ liệu tái lập.")


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--no-pause", action="store_true")
    parser.add_argument("--offline", action="store_true", help="Dùng suite và log đã lưu ở bước 2, 6")
    parser.add_argument("--dry-run", action="store_true", help="Chỉ in trình tự và lệnh")
    parser.add_argument("--step", type=int, choices=range(1, 11), help="Chạy riêng một bước")
    args = parser.parse_args(argv)
    for number, title in enumerate(TITLES, 1):
        if args.step and args.step != number:
            continue
        print(f"\nBước {number}/10 — {title}", flush=True)
        if args.dry_run:
            flags = " --offline" if args.offline else ""
            print(f"python experiments/scripts/demo.py --step {number}{flags}")
            if number == 6:
                print(f"python tools/generate_p4.py --run 1 --demo --start-suite test-suites/smoke{flags}")
        else:
            try:
                step(number, args.offline)
            except (OSError, ValueError, RuntimeError, subprocess.CalledProcessError) as exc:
                print(f"Bước {number}: {exc}", file=sys.stderr)
                return 1
        if not args.no_pause and not args.step and number < len(TITLES):
            input("Nhấn Enter để tiếp tục… ")
    return 0


if __name__ == "__main__":
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    sys.exit(main())
