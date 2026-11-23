# T18 – Thí nghiệm bóc tách "ngữ cảnh hạn chế" (P3r → P4r → P4br)

**Worker:** codex, effort `high` · **Nhánh:** `feature/reduced-context` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/orchestration/decisions.md` (D-018, D-020, **D-024**), `tools/generate_p3.py`, `tools/generate_p4.py`, `tools/svv/feedback.py`, `prompts/`, `experiments/scripts/run_experiment.py`, `experiments/scripts/analyze.py`, `experiments/config/default.yaml`.

## Mục tiêu

P3 đủ ngữ cảnh (đặc tả + mã nguồn) đã đạt 100% coverage ở mọi lần chạy, nên vòng phản hồi không có gì để cải thiện. Thí nghiệm bóc tách đo tác dụng của phản hồi khi lần sinh đầu **không thấy mã nguồn**.

## Phạm vi

- `prompts/direct-spec-only/` (system, generate, README): giống `prompts/direct/` nhưng **không có `{{SOURCE}}`**; LLM chỉ nhận API đóng băng và đặc tả. Câu chữ còn lại giữ tối đa như `prompts/direct/` để so sánh công bằng.
- `prompts/coverage-guided-reduced/`, `prompts/mutation-feedback-reduced/` (nếu cần mẫu riêng): **không có `{{SOURCE}}` toàn bộ**; phản hồi chỉ gồm đoạn mã quanh dòng/nhánh chưa phủ (hoặc quanh mutant còn sống) do `feedback.py` cắt, kèm đặc tả và API.
- `tools/generate_p3.py`, `tools/generate_p4.py`: thêm biến thể qua tham số, **không nhân bản file**. Ví dụ `generate_p3.py --variant spec-only` ghi vào `test-suites/p3r-spec-only/run-XX`; `generate_p4.py --variant reduced [--mutation-feedback]` đọc bộ P3r/P4r cùng `run-XX` và ghi `p4r-coverage-guided` / `p4br-mutation-feedback`. Mặc định giữ nguyên hành vi hiện tại.
- `experiments/scripts/run_experiment.py`: thêm 3 phương pháp, thứ tự ghép cặp `p3r → p4r → p4br` cho từng `run-XX`.
- `experiments/scripts/analyze.py`: thêm các cặp so sánh `p4r vs p3r`, `p4br vs p4r`, `p3r vs p3`, và bảng trước/sau cho chuỗi rút gọn; đường cải thiện qua các vòng cho P4r/P4br.
- `experiments/schema/run-result.schema.json`: chỉ khi cần cho tên phương pháp mới.
- `experiments/config/default.yaml`: chỉ thêm khóa mới nếu cần (dùng chung ngân sách/ngưỡng của `p4`).
- `tools/tests/**`

## Kiểm tra (không gọi LLM thật)

- `python -m pytest tools/tests -q` xanh, gồm test: prompt spec-only **không chứa** nội dung bất kỳ file `subject/src/main/java/shop/*.java` (kiểm bằng cách tìm một số dòng đặc trưng của mã nguồn trong prompt đã dựng); prompt P4r chỉ chứa đoạn mã quanh dòng chưa phủ; `run_experiment.py --dry-run --methods p3r-spec-only,p4r-coverage-guided,p4br-mutation-feedback --runs 1-2` đúng thứ tự ghép cặp.
- `python tools/generate_p3.py --run 1 --variant spec-only --dry-run` → in kích thước prompt (phải nhỏ hơn rõ rệt so với 59.804 ký tự của P3).
- Ghi trong báo cáo các lệnh chính xác để orchestrator chạy thật.
