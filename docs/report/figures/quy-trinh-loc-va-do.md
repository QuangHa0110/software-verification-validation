# Hình 3.3: Quy trình lọc test bốn tầng, chứng minh oracle và chọn tham lam lười

Sơ đồ trình bày chi tiết cách `tools/svv/test_filter.py` kiểm chứng từng khối test do LLM sinh ra trên hai phiên bản cài đặt độc lập (`reference/` và `subject/`), ghi nhận lý do loại để tính tỷ lệ biên dịch (`compile_rate`) và tỷ lệ oracle đúng (`oracle_rate`), đồng thời thể hiện bước chọn tham lam lười (`tools/svv/selection.py`) áp dụng cho các chiến lược phản hồi (P4, P4b, P4r, P4br).

```mermaid
flowchart TD
    RAW["Phản hồi thô của LLM\n(các khối ```java)"] --> EXT["extract_blocks()\nTách từng phương thức @Test / @ParameterizedTest"]
    EXT --> WRAP["wrap_block()\nBọc thành lớp shop.<Prefix>_NNNTest"]
    WRAP --> B1{"Bước 1: Biên dịch trên subject/\n(mvn -Djacoco.skip=true test-compile)"}

    B1 -->|"Lỗi biên dịch\n(cô lập từng file / chia đôi)"| D1["Loại: compile_error"]
    B1 -->|"Biên dịch thành công\n(tính compile_rate)"| B2{"Bước 2: Chạy trên reference/\n(cài đặt độc lập từ đặc tả)"}

    B2 -->|"Thất bại / lỗi / bỏ qua"| D2["Loại: fails_on_reference\n(Oracle sai so với đặc tả)"]
    B2 -->|"Xanh trên reference/\n(tính oracle_rate)"| B3{"Bước 3: Chạy trên subject/\n(bảo đảm bộ test xanh cho PIT)"}

    B3 -->|"Thất bại trên subject/"| D3["Loại: fails_on_subject_only\n(Bất đồng subject và reference)"]
    B3 -->|"Xanh trên cả hai bản"| B4{"Bước 4: Kiểm tra trùng lặp\nblock_signature() với bộ hiện có & đã giữ"}

    B4 -->|"Trùng chữ ký chuẩn hóa"| D4["Loại: duplicate"]
    B4 -->|"Duy nhất"| KEEP["Test hợp lệ qua bộ lọc\n(valid)"]

    D1 --> LOG["Ghi danh sách dropped\nvào generation.json và run-XX.json"]
    D2 --> LOG
    D3 --> LOG
    D4 --> LOG

    KEEP -->|"P3 / P3r"| SUITE["Ghi thành file .java riêng\nvào test-suites/<method>/run-XX/"]
    KEEP -->|"P4 / P4b / P4r / P4br"| SEL{"select_greedy() (max-heap)\nĐo gain() trên dự án Maven cô lập"}
    SEL -->|"gain == 0"| D5["Loại: no_coverage_gain /\nno_mutation_gain\n(ghi vào incremental.dropped_by_selection)"]
    SEL -->|"gain > 0"| SUITE
```
