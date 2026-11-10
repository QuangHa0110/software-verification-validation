# LLM-based Test Generation

**Sinh kiểm thử tự động bằng mô hình ngôn ngữ nhằm tối ưu hóa độ bao phủ mã nguồn**

Bài tập lớn học phần *Kiểm chứng và Thẩm định Phần mềm (Software Verification and Validation)* – hệ cao học, định hướng số 1.

- **Hệ thống áp dụng:** module giỏ hàng và thanh toán của hệ thống bán hàng trực tuyến (Java)
- **Phương pháp đề xuất:** sinh test lặp bằng LLM có phản hồi coverage, kết hợp phản hồi mutation
- **Hạn nộp:** 30/11/2026
- **Đề xuất đăng ký đề tài:** [docs/de-xuat-de-tai-1.md](docs/de-xuat-de-tai-1.md)

## Câu hỏi nghiên cứu

| Mã | Câu hỏi |
|---|---|
| RQ1 | LLM sinh test đạt statement và branch coverage ở mức nào? |
| RQ2 | Coverage-guided có cải thiện coverage so với Direct LLM không? |
| RQ3 | Test do LLM sinh có phát hiện lỗi tốt hơn Random Testing không? |
| RQ4 | Cung cấp thông tin nhánh chưa phủ có cải thiện chất lượng test không? |
| RQ5 | Mối quan hệ giữa số lượng test, coverage và mutation score? |
| RQ6 | Chi phí và thời gian sử dụng LLM có tương xứng với hiệu quả không? |

## Các phương pháp so sánh

| Mã | Phương pháp | Thư mục bộ test |
|---|---|---|
| P1 | Manual Testing – viết tay theo đặc tả (đồng thời là bộ test chuẩn) | `test-suites/p1-manual/` |
| P2 | Random Testing – đầu vào ngẫu nhiên, oracle lấy từ đặc tả | `test-suites/p2-random/` |
| P3 | Direct LLM Generation – sinh một lần từ mã nguồn và đặc tả | `test-suites/p3-direct-llm/` |
| P4 | Coverage-guided LLM Generation + mutation feedback | `test-suites/p4-coverage-guided/` |

## Cấu trúc thư mục

```
software-verification-validation/
├── docs/
│   ├── de-xuat-de-tai-1.md     # Đề xuất đăng ký đề tài
│   ├── spec/                   # Đặc tả chức năng, đầu vào/đầu ra (căn cứ cho oracle)
│   ├── literature/             # Ghi chú các công trình nghiên cứu liên quan
│   └── report/                 # Báo cáo khoa học (6 chương)
├── subject/                    # Chương trình đối tượng kiểm thử (Maven project)
│   └── src/
│       ├── main/java/          # Mã nguồn module giỏ hàng & thanh toán
│       └── test/java/          # Bộ test đang được chạy (copy từ test-suites/ khi thực nghiệm)
├── test-suites/                # Bộ test của từng phương pháp, lưu riêng để so sánh
│   ├── p1-manual/
│   ├── p2-random/
│   ├── p3-direct-llm/          # Mỗi lần chạy một thư mục con: run-01/, run-02/, ...
│   └── p4-coverage-guided/
├── tools/                      # Công cụ: sinh test ngẫu nhiên, gọi LLM, vòng lặp coverage-guided
├── prompts/                    # Bộ prompt (sản phẩm bắt buộc)
│   ├── direct/
│   ├── coverage-guided/
│   └── mutation-feedback/
├── experiments/
│   ├── config/                 # Cấu hình: model, nhiệt độ, số vòng lặp, ngân sách token, seed
│   └── scripts/                # Script chạy và tái lập toàn bộ thực nghiệm
└── results/
    ├── coverage/               # Báo cáo JaCoCo theo phương pháp / lần chạy
    ├── mutation/               # Báo cáo PIT, danh sách mutant tương đương
    ├── llm-logs/               # Prompt gửi đi, phản hồi, số token, chi phí từng lần gọi
    └── comparison/             # Bảng tổng hợp, biểu đồ, kết quả kiểm định thống kê
```

Thư mục rỗng được giữ trong git bằng file `.gitkeep`; xóa file này khi thư mục đã có nội dung.

## Công cụ

| Công cụ | Mục đích |
|---|---|
| Java 17, Maven | Ngôn ngữ và build |
| JUnit 5 | Unit testing |
| JaCoCo | Đo coverage (line, branch, method) |
| PIT (Pitest) | Mutation testing |
| LLM thương mại qua API | Sinh test (P3, P4) |

## Cài đặt và chạy

> Phần này sẽ được cập nhật khi `subject/pom.xml` và các script trong `experiments/scripts/` hoàn thiện.

API key của LLM đặt trong file `.env` ở thư mục gốc (đã nằm trong `.gitignore`, **không commit**):

```
LLM_API_KEY=...
```

Chạy test và đo coverage của chương trình đối tượng:

```bash
cd subject && mvn test jacoco:report
```

Chạy mutation testing:

```bash
cd subject && mvn org.pitest:pitest-maven:mutationCoverage
```

## Quản lý với git

**Nhánh**

- `main`: luôn ở trạng thái chạy được, chỉ nhận thay đổi đã hoàn chỉnh.
- Làm việc trên nhánh riêng theo phần việc, ví dụ:

| Nhánh | Nội dung |
|---|---|
| `feature/subject-program` | Chương trình đối tượng và đặc tả |
| `feature/measurement` | Cấu hình JaCoCo, PIT, script đo |
| `feature/random-testing` | P2 |
| `feature/llm-direct` | P3 và prompt direct |
| `feature/coverage-guided` | P4, bộ lọc test, mutation feedback |
| `experiment/run-YYYYMMDD` | Một đợt chạy thực nghiệm và kết quả |
| `docs/report` | Báo cáo khoa học |

**Commit message** theo dạng `<loại>: <mô tả>`:

| Loại | Dùng khi |
|---|---|
| `feat` | Thêm chức năng, công cụ |
| `fix` | Sửa lỗi |
| `test` | Thêm hoặc sửa bộ test |
| `prompt` | Thay đổi prompt |
| `exp` | Kết quả một lần chạy thực nghiệm |
| `docs` | Tài liệu, báo cáo |
| `chore` | Cấu hình, build, dọn dẹp |

Ví dụ: `feat: thêm vòng lặp coverage-guided`, `exp: kết quả P3 run-01..run-10`.

**Nguyên tắc để kết quả tái lập được**

- Mỗi lần chạy thực nghiệm commit **cùng lúc** bộ test sinh ra, cấu hình, log LLM và báo cáo kết quả.
- Không sửa tay bộ test do LLM sinh; nếu phải lọc, ghi lại test bị loại và lý do.
- Gắn tag cho các mốc quan trọng, ví dụ `v1.0-final` khi nộp bài.

## Kế hoạch

| Thời gian | Công việc |
|---|---|
| 10/11 – 12/11 | Đăng ký đề tài, chốt LLM, nghiên cứu tổng quan |
| 11/11 – 15/11 | Chương trình đối tượng, đặc tả, bộ test P1, quy trình đo |
| 16/11 – 20/11 | P2, P3 và vòng lặp coverage-guided của P4 |
| 21/11 – 23/11 | Bộ lọc test, mutation feedback |
| 24/11 – 26/11 | Chạy thực nghiệm 10 lần, thống kê |
| 27/11 – 29/11 | Báo cáo, demo, đóng gói |
| 30/11 | Nộp bài |
