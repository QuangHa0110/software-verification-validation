# T10 – P3 Direct LLM Generation và bộ prompt `prompts/direct/`

**Worker:** codex, effort `high` · **Nhánh:** `feature/llm-direct` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/orchestration/decisions.md` (D-005), `docs/thiet-ke-ky-thuat.md` mục 4.2, 6, 6.2, 6.3, 9; `tools/svv/llm_client.py`, `tools/svv/test_blocks.py`, `tools/svv/test_filter.py`, `experiments/spikes/s3/KET-QUA.md`.

## Phạm vi

- `prompts/direct/system.md`, `prompts/direct/generate.md` (mẫu prompt có chỗ thay thế `{{SPEC}}`, `{{SOURCE}}`, `{{API}}`); `prompts/direct/README.md` mô tả mục đích từng phần, biến thay thế, phiên bản. Xóa `prompts/direct/.gitkeep`.
- `tools/generate_p3.py`
- `tools/tests/test_generate_p3.py`
- `experiments/config/default.yaml`: chỉ thêm khóa `p3` nếu cần

## Yêu cầu

1. **Prompt** gửi **một lần** (đúng nghĩa Direct): toàn bộ `docs/spec/*.md` (trừ `open-questions.md`, nhưng **gồm** nội dung các quyết định trong đó nếu cần cho oracle: chọn và ghi lý do trong README của prompt) **và** toàn bộ mã nguồn `subject/src/main/java/shop/*.java`. Yêu cầu LLM:
   - viết test JUnit 5 cho package `shop`, mục tiêu phủ tối đa câu lệnh và nhánh, **oracle lấy theo đặc tả** (đặc tả là chuẩn đúng nếu mã và đặc tả khác nhau);
   - định dạng trả về đúng quy ước của `test_blocks.py`: mỗi test một khối ```` ```java ```` chứa đúng một phương thức test tự chứa, không hàm phụ, không trường lớp, không gọi `now()`;
   - không dùng công cụ, không chạy lệnh, không đọc file (toàn bộ ngữ cảnh đã có trong prompt).
   Không ghi số lượng test cố định trong prompt; để LLM tự quyết.
2. **`generate_p3.py --run N`**: dựng prompt từ mẫu, gọi `llm_client.complete(..., method="p3-direct-llm", run=N, purpose="direct")`, tách khối, chạy `test_filter`, ghi các test giữ lại vào `test-suites/p3-direct-llm/run-XX/`, cùng `generation.json` (thiết kế 6) có thêm `filter`: `generated`, `compiled`, `passed_reference`, `valid`, `compile_rate`, `oracle_correct_rate`, `dropped` (tên + lý do). Có cờ `--dry-run` chỉ in prompt và kích thước (số ký tự), không gọi LLM.
3. Thứ tự trong prompt cố định để lặp lại được; mẫu prompt được lưu kèm **hash SHA-256** trong `generation.json`.
4. Không sửa test do LLM sinh. Bộ lọc chỉ được bọc khối vào lớp (thiết kế 6.2).

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` xanh (dựng prompt, thay biến, ghi `generation.json` với LLM giả lập).
- `python tools/generate_p3.py --run 1 --dry-run` → in kích thước prompt.
- **Một lần chạy thật**: `python tools/generate_p3.py --run 1`. Báo cáo đúng như output: số khối sinh ra, số biên dịch được, số qua reference, số giữ lại, các lý do loại, token, thời gian. Có `test-suites/p3-direct-llm/run-01/`, `generation.json`, `results/llm-logs/p3-direct-llm/run-01/calls.jsonl`.
- `python experiments/scripts/measure.py --suite test-suites/p3-direct-llm/run-01 --method p3-direct-llm --run 1` → báo cáo exit code và coverage đúng như output. Sau đó xóa kết quả đo trong `results/comparison`, `results/coverage`, `results/mutation` (đo chính thức ở T14); **giữ** bộ test, `generation.json` và log LLM của run-01.
- Chỉ chạy thật **một** lần (`run-01`). Các lần 02–10 làm ở T14.
