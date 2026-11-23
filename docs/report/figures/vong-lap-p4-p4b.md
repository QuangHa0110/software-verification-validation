# Hình 3.2: Thiết kế ghép cặp P3 → P4 → P4b và vòng lặp phản hồi

Sơ đồ minh họa cơ chế kế thừa bộ test theo từng lần chạy (`run-XX`) giữa ba chiến lược LLM (quyết định D-003, D-020): P3 sinh bộ nền một lượt, P4 kế thừa P3 và lặp theo phản hồi coverage, P4b kế thừa bộ cuối của P4 và lặp theo phản hồi mutation.

```mermaid
flowchart TD
    START(["Bắt đầu lần chạy run-XX"]) --> P3["P3: Sinh trực tiếp 1 lượt\n(đặc tả + mã nguồn)"]
    P3 --> F3["Lọc 4 tầng (test_filter.py)\nBiên dịch → reference/ → subject/ → Loại trùng"]
    F3 --> S3["Bộ test nền S_P3\n(test-suites/p3-direct-llm/run-XX/)"]

    S3 --> P4_INIT["Khởi tạo P4: S ← S_P3\n(cộng dồn token & thời gian từ P3)"]
    P4_INIT --> P4_MEAS["Đo coverage bằng JaCoCo\n(LINE, BRANCH, METHOD)"]
    P4_MEAS --> P4_STOP{"Đạt điều kiện dừng P4?\n(branch >= target, hết vòng,\nhết token_budget, hoặc không tăng)"}
    P4_STOP -->|"Không"| P4_FB["Trích dòng/nhánh chưa phủ\nkèm ngữ cảnh mã nguồn"]
    P4_FB --> P4_LLM["Gọi LLM sinh test bổ sung\n(kèm danh sách tên test hiện có)"]
    P4_LLM --> P4_FILT["Lọc 4 tầng + Chọn tham lam\n(chỉ giữ test tăng coverage)"]
    P4_FILT --> P4_UPD["Gộp vào S; ghi bản ghi iterations"]
    P4_UPD --> P4_MEAS

    P4_STOP -->|"Có"| S4["Bộ test cuối S_P4\n(test-suites/p4-coverage-guided/run-XX/)"]

    S4 --> P4B_INIT["Khởi tạo P4b: S ← S_P4\n(cộng dồn token & thời gian từ P4)"]
    P4B_INIT --> P4B_MEAS["Đo mutation (PIT + mutate_custom.py)\ntrên bộ hiện tại S"]
    P4B_MEAS --> P4B_STOP{"Đạt điều kiện dừng P4b?\n(score >= target, hết vòng,\nhết token_budget, hoặc không tăng)"}
    P4B_STOP -->|"Không"| P4B_FB["Trích mutant SURVIVED và NO_COVERAGE\n(vị trí, toán tử, mô tả / đoạn mã đổi)"]
    P4B_FB --> P4B_LLM["Gọi LLM sinh test diệt mutant"]
    P4B_LLM --> P4B_FILT["Lọc 4 tầng + Chọn tham lam\n(chỉ giữ test diệt thêm mutant)"]
    P4B_FILT --> P4B_UPD["Gộp vào S; ghi bản ghi iterations"]
    P4B_UPD --> P4B_MEAS

    P4B_STOP -->|"Có"| S4B["Bộ test cuối S_P4b\n(test-suites/p4b-mutation-feedback/run-XX/)"]
    S4B --> END(["Đo tổng hợp cuối bằng measure.py --mutation"])
```
