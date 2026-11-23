# Hình 3.1: Kiến trúc tổng thể hệ thống sinh và đánh giá kiểm thử bằng LLM

Sơ đồ dưới đây mô tả luồng dữ liệu khép kín giữa đặc tả, mã nguồn đối tượng, mô hình ngôn ngữ lớn (LLM), bộ lọc kiểm chứng hai tầng (`reference/` và `subject/`), hạ tầng đo lường cô lập (JaCoCo, PIT, `mutate_custom.py`) và vòng lặp phản hồi cho P3, P4, P4b.

```mermaid
flowchart LR
    subgraph Input["1. Đầu vào cố định (M1)"]
        SPEC["Đặc tả nghiệp vụ\n(docs/spec/*.md)"]
        SRC["Mã nguồn đối tượng\n(subject/src/main/java/shop/)"]
    end

    subgraph Gen["2. Sinh test bằng LLM"]
        PROMPT["Bộ tạo prompt\n(prompts/direct, coverage-guided,\nmutation-feedback)"]
        LLM["Bộ gọi LLM (llm_client.py)\ncodex-cli / agy-cli / anthropic"]
        LOG["Nhật ký lời gọi\n(results/llm-logs/.../calls.jsonl)"]
    end

    subgraph Filter["3. Tách khối và lọc (test_blocks.py, test_filter.py)"]
        PARSE["Tách khối @Test độc lập\nvà bọc lớp shop.<Prefix>_NNNTest"]
        COMP["Bước 1: Biên dịch\ntrên subject/"]
        REF["Bước 2: Kiểm chứng oracle\ntrên reference/ độc lập"]
        SUBJ["Bước 3: Chạy xác nhận\ntrên subject/"]
        DEDUP["Bước 4: Loại trùng lặp\n(chuẩn hóa thân hàm)"]
    end

    subgraph Measure["4. Đo lường cô lập (measure.py trên .work/)"]
        JUNIT["JUnit 5 + JaCoCo 0.8.14\n(LINE, BRANCH, METHOD)"]
        MUT["PIT 1.20.3 + mutate_custom.py\n(5 toán tử đột biến)"]
        SEL["Chọn test tham lam\n(tăng coverage / diệt thêm mutant)"]
    end

    subgraph Feedback["5. Trích xuất phản hồi (P4 / P4b)"]
        FB_COV["Nhánh và dòng chưa phủ\nkèm ngữ cảnh mã nguồn (P4)"]
        FB_MUT["Mutant còn sống\nSURVIVED / NO_COVERAGE (P4b)"]
    end

    OUT["Bộ test giữ lại (test-suites/)\nvà số liệu đo (results/comparison/raw/)"]

    SPEC --> PROMPT
    SRC --> PROMPT
    PROMPT --> LLM
    LLM --> LOG
    LLM --> PARSE
    PARSE --> COMP --> REF --> SUBJ --> DEDUP
    DEDUP --> SEL
    SEL --> JUNIT
    JUNIT --> MUT
    JUNIT --> FB_COV
    MUT --> FB_MUT
    FB_COV -->|"Vòng lặp P4"| PROMPT
    FB_MUT -->|"Vòng lặp P4b"| PROMPT
    JUNIT --> OUT
    MUT --> OUT
```
