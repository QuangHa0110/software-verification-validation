# T20 – Kịch bản demo 10 bước, script demo và slide

**Worker:** codex, effort `medium` · **Nhánh:** `feature/demo` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/ke-hoach-thuc-hien.md` mục 8 (kịch bản demo 10 bước), `docs/orchestration/decisions.md` (D-024, D-025), `tools/generate_p3.py`, `tools/generate_p4.py` (chế độ `--demo`), `experiments/scripts/measure.py`, `experiments/scripts/analyze.py`, `docs/report/`.

## Phạm vi

- `tools/generate_p4.py`: thêm tùy chọn `--start-suite <thư mục>` **chỉ dùng cùng `--demo`**, cho phép vòng lặp demo khởi đầu từ một bộ test bất kỳ (ví dụ `test-suites/smoke/`) thay vì bộ P3; kết quả demo ghi vào `.work/demo/` (không ghi `test-suites/`).
- `experiments/scripts/demo.py`: chạy tuần tự các bước demo, có tạm dừng chờ Enter giữa các bước (cờ `--no-pause` để chạy liền), in rõ tiêu đề từng bước. Các bước gọi lại công cụ có sẵn, không sao chép logic.
- `docs/demo/kich-ban-demo.md`: kịch bản 10 bước theo kế hoạch mục 8, mỗi bước: mục tiêu, lệnh chính xác, output mong đợi **dạng mô tả** (không ghi số cụ thể chưa chạy), thời lượng ước tính, phương án dự phòng khi LLM chậm/hết quota (dùng kết quả có sẵn trong `results/`).
- `docs/demo/slides.md`: slide dạng Marp Markdown (≈ 15–20 slide) bám theo báo cáo: bài toán, câu hỏi nghiên cứu, chương trình đối tượng, phương pháp P1–P4b và hai thí nghiệm bóc tách, kiến trúc, quy trình đo, **chỗ để số liệu** đánh dấu `[SỐ LIỆU: results/comparison/summary.md]`, hạn chế (hiệu ứng trần), đóng góp.
- `tools/tests/**`: test cho `--start-suite` (chỉ hợp lệ với `--demo`) và `demo.py --no-pause --dry-run`.

## Yêu cầu

1. Bước 2 (dùng LLM sinh test) và bước 6 (đưa coverage cho LLM) có cờ `--offline` dùng bộ test và log đã có sẵn thay vì gọi LLM, để demo chạy được khi không có mạng/quota.
2. Bước 5–7 mặc định chạy `generate_p4.py --demo --start-suite test-suites/smoke` để có nhánh chưa phủ thật mà trình diễn.
3. Không gọi LLM thật khi kiểm tra (mục 4b). Kiểm tra bằng `--offline` và `--dry-run`.

## Kiểm tra

- `python -m pytest tools/tests -q` xanh.
- `python experiments/scripts/demo.py --no-pause --offline` chạy hết các bước không cần LLM; báo cáo output thật (rút gọn).
- Ghi lệnh chính xác để orchestrator chạy thử demo có LLM thật.
