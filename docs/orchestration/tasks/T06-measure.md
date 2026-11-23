# T06 – Cấu hình thực nghiệm, thư viện đọc báo cáo và `measure.py`

**Worker:** codex, effort `high` · **Nhánh:** `feature/measurement` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 2, 4.2, 5, 6; `experiments/spikes/s1-s2/KET-QUA.md`; `subject/pom.xml`.

## Phạm vi

- `experiments/config/default.yaml`: nội dung theo thiết kế 2.3. Thêm khóa `llm.provider: codex-cli`, `llm.model: gpt-6.2-astra`, và `measure.pit_mutators: [CONDITIONALS_BOUNDARY, MATH, VOID_METHOD_CALLS, EXPERIMENTAL_MEMBER_VARIABLE]` (quyết định D-012 trong `docs/orchestration/decisions.md`; **không** dùng `INLINE_CONSTS`). Xóa `experiments/config/.gitkeep`.
- `experiments/schema/run-result.schema.json`: JSON Schema (draft 2020-12) cho lược đồ thiết kế 5.1.
- `tools/svv/__init__.py`, `tools/svv/config.py`, `tools/svv/jacoco.py`, `tools/svv/surefire.py`, `tools/svv/pit.py`, `tools/svv/maven.py`, `tools/svv/workdir.py`: thư viện dùng chung (G5 sẽ dùng lại `jacoco.py` để lấy nhánh chưa phủ).
- `experiments/scripts/measure.py`. Xóa `experiments/scripts/.gitkeep`.
- `tools/tests/**`: test pytest cho thư viện, kèm file XML mẫu nhỏ trong `tools/tests/fixtures/`.
- `results/mutation/equivalent-mutants.csv`: chỉ dòng tiêu đề `source,class,method,line,mutator,description,reason`.

## Yêu cầu

1. **`config.py`**: đọc `experiments/config/default.yaml`; cho phép file ghi đè `experiments/config/*.local.yaml` (đã bị `.gitignore` chặn).
2. **`workdir.py`**: tạo `.work/<id>/` bằng cách sao chép `subject/` (bỏ `target/`), chép các file `.java` của bộ test vào `.work/<id>/src/test/java/shop/`; tham số chọn nguồn chương trình (`subject` hoặc `reference`) để bộ lọc dùng lại sau. Xóa thư mục tạm khi xong (có cờ giữ lại để gỡ lỗi).
3. **`maven.py`**: chạy `mvn -B` bằng `subprocess` (không dùng shell), đo thời gian, trả exit code, stdout, stderr. Trên Windows gọi `mvn.cmd`.
4. **`jacoco.py`**: đọc `jacoco.xml`, **chỉ tính lớp trong package `shop`**. Trả LINE (đại diện statement), BRANCH, METHOD (đại diện function): covered, total, pct. Trả danh sách dòng chưa phủ (`class`, `method`, `line`) và nhánh chưa phủ hết (`class`, `method`, `line`, `missed`, `covered`) từ các phần tử `<line mi ci mb cb>` của `<sourcefile>`, gán `method` bằng cách đối chiếu dòng với `<method line=...>`.
5. **`surefire.py`**: đọc `target/surefire-reports/TEST-*.xml`: số test, failures, errors, skipped; tên test fail.
6. **`pit.py`**: đọc `mutations.xml`: total, killed, survived, no_coverage, timed_out (và các trạng thái khác, ghi rõ cách gộp); `by_operator` gộp theo **5 toán tử của đề** với khóa cố định `relational` (CONDITIONALS_BOUNDARY), `arithmetic` (MATH), `conditional` (chỉ từ mutant tự tạo), `boolean_constant` (chỉ từ mutant tự tạo), `statement_deletion` (VOID_METHOD_CALLS, EXPERIMENTAL_MEMBER_VARIABLE và mutant tự tạo); mutator khác để ở nhóm `other`. Mỗi mục `by_operator` ghi thêm `source` (`pit`, `custom` hoặc cả hai). Thiết kế `pit.py` sao cho T07 cộng được kết quả mutant tự tạo vào cùng cấu trúc (hàm gộp hai nguồn). Áp dụng `equivalent-mutants.csv` (khớp theo `class`, `method`, `line`, `mutator`, `description`). **Score = killed / (total − equivalent) × 100**; `NO_COVERAGE` nằm trong mẫu số; `TIMED_OUT` và `MEMORY_ERROR` tính là killed (ghi rõ trong docstring, đây là quy ước của PIT).
7. **`measure.py`**:
   ```
   python experiments/scripts/measure.py --suite <thư mục bộ test> --method <mã> --run <n> [--mutation] [--program subject|reference] [--keep-workdir]
   ```
   Chạy `mvn -B test` (JaCoCo), và PIT nếu có `--mutation` với `-Dpit.mutators=<từ cấu hình>`. Đọc `generation.json` cạnh bộ test nếu có (thiết kế 6). Ghi `results/comparison/raw/<method>/run-XX.json` đúng lược đồ và **kiểm tra bằng schema trước khi ghi**. Chép báo cáo JaCoCo (XML + HTML) vào `results/coverage/<method>/run-XX/`, báo cáo PIT vào `results/mutation/<method>/run-XX/`. Exit 0 khi đo xong; exit khác 0 (và in lý do) khi bộ test không xanh trên chương trình. Với `--program reference`, chỉ chạy test (không đo, không ghi kết quả), in số pass/fail: dùng để đối chiếu subject và reference.
8. **pytest** (`tools/tests/`): parser JaCoCo (tổng, lọc package, dòng/nhánh chưa phủ), parser PIT (đếm trạng thái, gộp toán tử, mutant tương đương, công thức score), surefire, kiểm tra schema với một kết quả mẫu. Chạy bằng `python -m pytest tools/tests -q`.

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` → xanh.
- `python experiments/scripts/measure.py --suite test-suites/smoke --method smoke --run 1 --mutation` → exit 0, có `results/comparison/raw/smoke/run-01.json` đúng schema. **Đối chiếu tay**: LINE và BRANCH covered/total trong JSON phải bằng số ở dòng `Total` của `results/coverage/smoke/run-01/` (HTML hoặc XML); nêu cả hai số trong báo cáo.
- Sau khi kiểm tra, xóa `results/comparison/raw/smoke/`, `results/coverage/smoke/`, `results/mutation/smoke/` (bộ smoke không phải phương pháp thực nghiệm).
