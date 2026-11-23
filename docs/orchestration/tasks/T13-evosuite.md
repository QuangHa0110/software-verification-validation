# T13 – Spike S4 và đối chứng EvoSuite

**Worker:** codex, effort `medium` · **Nhánh:** `feature/evosuite` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 6 (dòng `evosuite`), 7 (S4), 9; `experiments/scripts/measure.py`, `experiments/config/default.yaml`.

## Phạm vi

- `experiments/spikes/s4/KET-QUA.md`
- `tools/generate_evosuite.py`, `tools/tests/test_generate_evosuite.py` (chỉ khi S4 đạt)
- `experiments/config/default.yaml`: chỉ thêm khóa `evosuite` (chỉ khi S4 đạt)
- `test-suites/evosuite/run-XX/` (chỉ khi S4 đạt)

Jar EvoSuite tải về để trong `.work/evosuite/` (đã bị `.gitignore` chặn), **không** commit jar.

## Việc cần làm

1. **S4**: tải bản phát hành EvoSuite mới nhất từ GitHub Releases chính thức (`EvoSuite/evosuite`), ghi phiên bản và SHA-256. Thử sinh test cho **một** lớp (`shop.ShippingCalculator`) của `subject/` đã biên dịch, với JDK 17 có sẵn. Nếu lỗi, thử các tùy chọn hợp lý trong tối đa **3 lần** (ví dụ `--add-opens`, chạy runtime ở chế độ tương thích). **Không** cài JDK khác.
2. Ghi `experiments/spikes/s4/KET-QUA.md`: lệnh đã chạy, output (trích nguyên văn lỗi nếu có), kết luận **đạt / không đạt** và lý do. Chỉ kết luận từ output thật.
3. **Chỉ khi đạt**: `generate_evosuite.py --run N` sinh test cho mọi lớp `shop`, seed = `random.seed_base + N`, ngân sách thời gian mỗi lớp lấy từ cấu hình. Chuyển test sang dạng chạy được với JUnit 5 của `subject/pom.xml` mà **không cần runtime EvoSuite** (nếu không làm được thì ghi rõ, coi như S4 không đạt). Ghi `test-suites/evosuite/run-XX/` và `generation.json`. Chạy thật `run-01`, đo bằng `measure.py --suite test-suites/evosuite/run-01 --method evosuite --run 1`, báo cáo đúng như output, rồi xóa kết quả đo trong `results/`.
4. **Nếu không đạt**: dừng sau bước 2. Đây là kết quả hợp lệ (thiết kế cho phép bỏ EvoSuite nếu ghi lý do).
