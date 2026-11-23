# T11, T12 – P4 Coverage-guided và P4b phản hồi mutation

**Worker:** codex, effort `xhigh` · **Nhánh:** `feature/coverage-guided` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 2.3, 5.1, 6.1, 6.2, 9; `tools/svv/` (đặc biệt `jacoco.py`, `pit.py`, `custom_mutation.py`, `llm_client.py`, `test_blocks.py`, `test_filter.py`); `tools/generate_p3.py`; `prompts/direct/`; `experiments/scripts/measure.py`.

## Phạm vi

- `prompts/coverage-guided/*.md`, `prompts/mutation-feedback/*.md` (mẫu prompt + README mô tả biến thay thế). Xóa `.gitkeep` tương ứng.
- `tools/generate_p4.py`, `tools/svv/feedback.py` (dựng ngữ cảnh nhánh chưa phủ / mutant còn sống), `tools/svv/selection.py` (chọn test tham lam)
- `tools/tests/test_feedback.py`, `tools/tests/test_selection.py`, `tools/tests/test_generate_p4.py`
- `experiments/config/default.yaml`: chỉ thêm khóa mới trong `p4` (ví dụ `p4b.max_iterations`, `p4b.token_budget`)

## Thiết kế (theo thiết kế 6.1, đã chốt ở D-003)

1. **P4** `run-XX` khởi đầu từ **bộ test giữ lại của P3 `run-XX`** (`test-suites/p3-direct-llm/run-XX/`). Thiếu bộ P3 thì báo lỗi rõ ràng, không tự sinh.
2. Vòng lặp (mọi tham số lấy từ `p4` trong cấu hình):
   1. Đo coverage bộ hiện tại (không mutation), dùng `tools/svv/` (có thể gọi lại hàm của `measure.py`).
   2. Dừng khi `branch_pct >= target_branch_coverage`, hết `max_iterations`, tổng token vượt `token_budget`, hoặc `stop_if_no_gain_iterations` vòng liên tiếp không tăng (LINE hoặc BRANCH). Ghi lý do dừng.
   3. `feedback.py`: với mỗi dòng/nhánh chưa phủ, lấy **mã nguồn quanh dòng đó** (vài dòng ngữ cảnh, đánh số dòng), tên phương thức bao quanh, số nhánh đã phủ/chưa phủ. Gộp theo phương thức để prompt gọn.
   4. Prompt `coverage-guided` gồm: đặc tả, mã nguồn, **danh sách test hiện có (chỉ tên phương thức)** để tránh trùng, và ngữ cảnh nhánh chưa phủ. Yêu cầu cùng định dạng khối như P3.
   5. Lọc bằng `test_filter` (loại trùng so với bộ hiện có).
   6. `selection.py`: chỉ **giữ test làm tăng coverage**, chọn tham lam theo mức đóng góp (số dòng + nhánh mới phủ). Cần coverage riêng của từng test ứng viên: chọn cách khả thi (ví dụ đo mỗi test ứng viên cộng bộ hiện tại, hoặc dùng JaCoCo per-test bằng session), ghi rõ cách đã chọn và độ phức tạp thời gian.
   7. Gộp test được giữ, ghi một dòng vào `iterations`: `i`, `statement_pct`, `branch_pct`, `kept_tests`, `tokens`, `generated`, `valid`, `dropped_by_reason`.
3. **P4b** (`--mutation-feedback`, phương pháp `p4b-mutation-feedback`) khởi đầu từ **bộ cuối của P4 `run-XX`**. Bước 1 đo thêm mutation (PIT + mutant tự tạo), bước 3 dùng **mutant còn sống** (`SURVIVED` và `NO_COVERAGE`): vị trí, toán tử, mô tả, đoạn mã gốc và đoạn đã đổi (với mutant tự tạo), mô tả của PIT (với mutant PIT). Bước 6 giữ test **diệt thêm mutant** (tham lam theo số mutant diệt thêm; test vừa tăng coverage vừa diệt thêm đều tính). Điều kiện dừng dùng mutation score thay cho branch, tham số riêng trong cấu hình.
4. Đầu ra: `test-suites/<phương pháp>/run-XX/` (bộ cuối cùng, **gồm cả test kế thừa từ P3/P4**, mỗi test giữ file gốc), `generation.json` với `iterations`, tổng thời gian, tổng token **cộng dồn cả phần của P3** (và của P4 đối với P4b) theo thiết kế 6.1, kèm tách riêng phần phát sinh thêm. Log LLM tại `results/llm-logs/<phương pháp>/run-XX/calls.jsonl`.
5. **Chế độ demo** `--demo`: dùng `demo_max_iterations`, in tiến trình từng vòng ra màn hình (coverage trước/sau, số test giữ), ghi vào thư mục tạm `.work/demo/` thay vì `test-suites/` trừ khi có `--output`.

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` xanh (feedback từ fixture JaCoCo/PIT; selection với dữ liệu giả; vòng lặp với LLM giả lập và điều kiện dừng).
- **Không chạy LLM thật** (D-018, `worker-rules.md` mục 4b). Thay vào đó: test pytest end-to-end với LLM giả lập trên bộ P3 có sẵn (`test-suites/p3-direct-llm/run-01/` nếu đã có, nếu chưa thì dùng một bộ nhỏ cố ý thiếu nhánh lấy từ `test-suites/smoke/`), chạy Maven thật: (a) P4 2 vòng, LLM giả lập trả một test phủ thêm nhánh và một test vô ích → test vô ích bị loại, `iterations` có 2 dòng, coverage không giảm; (b) P4b 1 vòng, LLM giả lập trả một test diệt thêm mutant → được giữ. Đánh dấu `@pytest.mark.maven`.
- Có tùy chọn `--provider/--model/--effort` để orchestrator ghi đè cấu hình khi chạy thật; timeout mỗi lời gọi lấy từ `p4.timeout_s` (đặt 1800).
- Cấu hình (D-020): đặt `p4.target_branch_coverage: 100`, `p4.token_budget: 400000`; thêm `p4b.max_iterations: 3`, `p4b.token_budget: 300000`, `p4b.target_mutation_score: 100`, `p4b.stop_if_no_gain_iterations: 2`. `token_budget` tính bằng `input_tokens + output_tokens` của các lời gọi trong lần chạy đó.
- Ghi trong báo cáo lệnh chính xác để orchestrator chạy thật `--demo`, P4 và P4b cho `run-01`.
