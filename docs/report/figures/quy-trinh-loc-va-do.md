# Hình 3.3: Quy trình lọc test bốn tầng và chứng minh tính đúng oracle

Sơ đồ trình bày chi tiết cách `tools/svv/test_filter.py` kiểm chứng từng khối test do LLM sinh ra trên hai phiên bản cài đặt độc lập (`reference/` và `subject/`), đồng thời ghi nhận chính xác lý do loại để tính tỷ lệ biên dịch (`compile_rate`) và tỷ lệ oracle đúng (`oracle_rate`).

```mermaid
flowchart TD
    RAW["Phản hồi thô của LLM\n(các khối ```java)"] --> EXT["extract_blocks()\nTách từng phương thức @Test / @ParameterizedTest"]
    EXT --> WRAP["wrap_block()\nBọc thành lớp shop.<Prefix>_NNNTest"]
    WRAP --> B1{"Bước 1: Biên dịch trên subject/\n(mvn test-compile)"}

    B1 -->|"Lỗi biên dịch\n(cô lập từng file / chia đôi)"| D1["Loại: compile_error"]
    B1 -->|"Biên dịch thành công\n(tính compile_rate)"| B2{"Bước 2: Chạy trên reference/\n(cài đặt độc lập từ đặc tả)"}

    B2 -->|"Thất bại / lỗi / bỏ qua"| D2["Loại: fails_on_reference\n(Oracle sai so với đặc tả)"]
    B2 -->|"Xanh trên reference/\n(tính oracle_rate)"| B3{"Bước 3: Chạy trên subject/\n(bảo đảm bộ test xanh cho PIT)"}

    B3 -->|"Thất bại trên subject/"| D3["Loại: fails_on_subject_only\n(Bất đồng subject và reference)"]
    B3 -->|"Xanh trên cả hai bản"| B4{"Bước 4: Kiểm tra trùng lặp\nblock_signature() với bộ đã giữ"}

    B4 -->|"Trùng chữ ký chuẩn hóa"| D4["Loại: duplicate"]
    B4 -->|"Duy nhất"| KEEP["Test hợp lệ được giữ lại\n(valid)"]

    D1 --> LOG["Ghi danh sách dropped\nvào generation.json và run-XX.json"]
    D2 --> LOG
    D3 --> LOG
    D4 --> LOG
    KEEP --> SUITE["Ghi thành file .java riêng\nvào test-suites/<method>/run-XX/"]
```
