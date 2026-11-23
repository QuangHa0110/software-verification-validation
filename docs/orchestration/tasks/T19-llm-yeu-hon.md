# T19 – Yếu tố "LLM yếu hơn" (P3w → P4w → P4bw) và prompt dài cho `agy-cli`

**Worker:** agy hoặc codex, effort cao · **Nhánh:** `feature/weak-llm` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/orchestration/decisions.md` (D-018, D-021, **D-025**), `tools/svv/llm_client.py`, `tools/generate_p3.py`, `tools/generate_p4.py`, `experiments/scripts/run_experiment.py`, `experiments/scripts/analyze.py`, `experiments/config/default.yaml`, và cách T18 đã thêm biến thể `spec-only`/`reduced` (mẫu để làm tương tự).

## Phạm vi

- `tools/svv/llm_client.py`: provider `agy-cli` gửi prompt qua file. Ghi prompt vào `prompt.md` (UTF-8) trong thư mục tạm của lần gọi, gọi `agy -p "<lệnh ngắn yêu cầu đọc prompt.md trong thư mục hiện tại và làm đúng theo đó, không dùng công cụ nào khác ngoài đọc file đó>" --model ... --output-format json --print-timeout ...` với `cwd` là thư mục tạm. Áp dụng khi prompt dài hơn ngưỡng cấu hình `llm.agy_inline_max_chars` (mặc định 8000); ngắn hơn thì giữ cách cũ. Log ghi thêm `prompt_via_file: true/false`; `tool_events` của agy (nếu JSON có `num_turns` > 1 thì ghi `num_turns`) để biết agy đã dùng công cụ đọc file.
- `tools/generate_p3.py`, `tools/generate_p4.py`: thêm `--variant weak-llm`, ghi vào `p3w-weak-llm`, `p4w-coverage-guided`, `p4bw-mutation-feedback`; dùng **cùng prompt đủ ngữ cảnh** như P3/P4/P4b; provider/model lấy từ khối cấu hình mới `llm_weak` (`provider: agy-cli`, `model: gemini-3.8-flash-low`, `effort: low`, `timeout_s: 1800`). Mặc định không đổi hành vi cũ.
- `experiments/scripts/run_experiment.py`: thêm chuỗi `p3w → p4w → p4bw`.
- `experiments/scripts/analyze.py`: thêm cặp `p4w vs p3w`, `p4bw vs p4w`, `p3w vs p3`; bảng trước/sau và đường qua các vòng cho chuỗi này.
- `experiments/config/default.yaml`: thêm `llm.agy_inline_max_chars` và khối `llm_weak`.
- `experiments/schema/run-result.schema.json`: chỉ khi cần.
- `tools/tests/**`

## Kiểm tra (không gọi LLM thật)

- `python -m pytest tools/tests -q` xanh, gồm: `agy-cli` với prompt dài ghi `prompt.md` đúng nội dung và gửi lệnh ngắn (kiểm bằng giả lập `subprocess.run`); prompt ngắn vẫn inline; `--variant weak-llm` dùng provider/model của `llm_weak`; dry-run chuỗi `p3w → p4w → p4bw` đúng thứ tự.
- `python tools/generate_p3.py --run 1 --variant weak-llm --dry-run` → in kích thước prompt và provider/model.
- Ghi trong báo cáo các lệnh chính xác để orchestrator chạy thật.
