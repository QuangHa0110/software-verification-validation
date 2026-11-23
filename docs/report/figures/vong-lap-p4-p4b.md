# Hình 3.2: Thiết kế ghép cặp P3 → P4 → P4b (và P3r → P4r → P4br) cùng vòng lặp phản hồi

Sơ đồ minh họa cơ chế kế thừa bộ test theo từng lần chạy (`run-XX`) giữa ba chiến lược LLM (quyết định D-003, D-020, D-024) khớp với mã nguồn `tools/generate_p3.py`, `tools/generate_p4.py` và `tools/svv/selection.py`: P3/P3r sinh bộ nền một lượt, P4/P4r kế thừa và lặp theo phản hồi coverage, P4b/P4br kế thừa bộ cuối của P4/P4r và lặp theo phản hồi mutation.

```mermaid
flowchart TD
    START(["Bắt đầu lần chạy run-XX\nChuỗi full (P3→P4→P4b) hoặc reduced (P3r→P4r→P4br)"]) --> P3["P3 (đủ ngữ cảnh) hoặc P3r (chỉ API + đặc tả)\nSinh trực tiếp 1 lượt (generate_p3.py)"]
    P3 --> F3["Lọc 4 tầng (test_filter.py)\nBiên dịch → reference/ → subject/ → Loại trùng"]
    F3 --> S3["Bộ test nền S_P3 / S_P3r\n(test-suites/p3*/run-XX/ + generation.json)"]

    S3 --> P4_INIT["Khởi tạo P4 / P4r: sao chép S_parent sang staging\nĐo baseline bằng JaCoCo; incremental.total_tokens = 0, no_gain = 0"]
    P4_INIT --> P4_STOP{"stopping_reason() cho P4 / P4r?\n1. branch_pct >= 100\n2. incremental.total_tokens >= 400k\n3. no_gain >= 2\n4. |iterations| >= 5 (hoặc 2 nếu --demo)"}
    P4_STOP -->|"Chưa dừng"| P4_FB["coverage_feedback() (feedback.py)\nGộp cửa sổ ±2 dòng quanh dòng/nhánh chưa phủ"]
    P4_FB --> P4_LLM["render_prompt() + llm_client.complete()\n(kèm TEST_NAMES; bỏ SOURCE ở P4r)\nCập nhật incremental.total_tokens"]
    P4_LLM --> P4_FILT["Lọc 4 tầng (prefix P4)\n+ select_greedy() max-heap (selection.py)"]
    P4_FILT --> P4_UPD["Ghi selected vào staging; cập nhật current\nGhi iterations[i]; no_gain = 0 nếu có chọn, ngược lại +1"]
    P4_UPD --> P4_STOP

    P4_STOP -->|"Dừng"| S4["Xuất nguyên tử (_publish) S_P4 / S_P4r\n(generation.json cộng dồn P3 + P4)"]

    S4 --> P4B_INIT["Khởi tạo P4b / P4br: sao chép S_parent sang staging\nĐo baseline (JaCoCo + PIT + mutate_custom + equivalents)\nincremental.total_tokens = 0, no_gain = 0"]
    P4B_INIT --> P4B_STOP{"stopping_reason() cho P4b / P4br?\n1. mutation_score >= 100\n2. incremental.total_tokens >= 300k\n3. no_gain >= 2\n4. |iterations| >= 3 (hoặc 2 nếu --demo)"}
    P4B_STOP -->|"Chưa dừng"| P4B_FB["mutation_feedback() (feedback.py)\nMutant SURVIVED và NO_COVERAGE đã trừ tương đương"]
    P4B_FB --> P4B_LLM["render_prompt() + llm_client.complete()\n(kèm TEST_NAMES; bỏ SOURCE ở P4br)\nCập nhật incremental.total_tokens"]
    P4B_LLM --> P4B_FILT["Lọc 4 tầng (prefix P4b)\n+ select_greedy() max-heap (diệt thêm mutant)"]
    P4B_FILT --> P4B_UPD["Ghi selected vào staging; cập nhật current\nGhi iterations[i]; no_gain = 0 nếu có chọn, ngược lại +1"]
    P4B_UPD --> P4B_STOP

    P4B_STOP -->|"Dừng"| S4B["Xuất nguyên tử (_publish) S_P4b / S_P4br\n(generation.json cộng dồn P3 + P4 + P4b)"]
    S4B --> END(["Đo tổng hợp chuẩn hóa bằng measure.py --mutation"])
```
