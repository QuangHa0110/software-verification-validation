# LLM-based Test Generation

**Sinh kiểm thử tự động bằng mô hình ngôn ngữ nhằm tối ưu hóa độ bao phủ mã nguồn**

Bài tập lớn học phần *Kiểm chứng và Thẩm định Phần mềm (Software Verification and Validation)* – hệ cao học, định hướng số 1.

- **Hệ thống áp dụng:** module giỏ hàng và thanh toán của hệ thống bán hàng trực tuyến (Java)
- **Phương pháp đề xuất:** sinh test lặp bằng LLM có phản hồi coverage, kết hợp phản hồi mutation
- **Hạn nộp:** 31/12/2026
- **Đề xuất đăng ký đề tài:** [docs/de-xuat-de-tai-1.md](docs/de-xuat-de-tai-1.md)
- **Kế hoạch và phân công nhóm:** [docs/ke-hoach-thuc-hien.md](docs/ke-hoach-thuc-hien.md)

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
| P2 | Random Testing – đầu vào ngẫu nhiên, oracle lấy từ chương trình tham chiếu | `test-suites/p2-random/` |
| P3 | Direct LLM Generation – sinh một lần từ mã nguồn và đặc tả | `test-suites/p3-direct-llm/` |
| P4 | Coverage-guided LLM Generation – khởi đầu từ P3 cùng lần chạy | `test-suites/p4-coverage-guided/` |
| P4b | P4 + phản hồi mutation (cải tiến của đề tài) | `test-suites/p4b-mutation-feedback/` |
| P3r → P4r → P4br | Thí nghiệm bóc tách ngữ cảnh hạn chế: P3r chỉ nhận đặc tả + API (D-024) | `test-suites/p3r-spec-only/`, `p4r-…`, `p4br-…` |
| P3w → P4w → P4bw | Thí nghiệm bóc tách LLM yếu hơn: `gemini-3.8-flash-low` qua `agy-cli` (D-025) | `test-suites/p3w-weak-llm/`, `p4w-…`, `p4bw-…` |

**Kết quả chính** (đã chốt, tag `m5-data-frozen`, nguồn [results/comparison/summary.md](results/comparison/summary.md)): P3/P4/P4b và chuỗi ngữ cảnh hạn chế đạt trần (100% nhánh, 100% mutation score sau khi loại 1 mutant tương đương) ở cả 10 lần; P2 đạt 76,78% nhánh, 64,33% mutation score; ở chuỗi LLM yếu hơn, phản hồi mutation nâng mutation score trung bình từ 95,00% (P4w) lên 98,50% (P4bw), Mann–Whitney p = 0,0001, Â12 = 1,0. Báo cáo đầy đủ: [docs/report/](docs/report/README.md).

## Cấu trúc thư mục

```
software-verification-validation/
├── docs/
│   ├── de-xuat-de-tai-1.md     # Đề xuất đăng ký đề tài
│   ├── spec/                   # Đặc tả chức năng, đầu vào/đầu ra (căn cứ cho oracle), chốt ở M1
│   ├── orchestration/          # Quy trình phát triển bằng AI: quyết định, tiến độ, brief giao việc
│   ├── literature/             # Ghi chú các công trình nghiên cứu liên quan
│   ├── demo/                   # Kịch bản demo 10 bước và slide (Marp)
│   └── report/                 # Báo cáo khoa học (6 chương)
├── subject/                    # Chương trình đối tượng kiểm thử (Maven project)
│   └── src/
│       ├── main/java/          # Mã nguồn module giỏ hàng & thanh toán
│       └── test/java/          # Luôn để trống; bộ test chạy qua measure.py hoặc profile Maven `suite`
├── reference/                  # Chương trình tham chiếu, viết độc lập từ đặc tả (chuẩn để kiểm tra oracle)
├── test-suites/                # Bộ test của từng phương pháp, lưu riêng để so sánh
│   ├── smoke/                  # Test khói của người cài đặt (không phải phương pháp thực nghiệm)
│   ├── p1-manual/
│   ├── p2-random/
│   ├── p3-direct-llm/          # Mỗi lần chạy một thư mục con: run-01/, run-02/, ...
│   ├── p4-coverage-guided/
│   └── p4b-mutation-feedback/  # P4 cộng phản hồi mutation (thí nghiệm bóc tách)
├── tools/                      # Công cụ: sinh test ngẫu nhiên, gọi LLM, vòng lặp coverage-guided
├── prompts/                    # Bộ prompt (sản phẩm bắt buộc)
│   ├── direct/                 # P3 (và P3w)
│   ├── direct-spec-only/       # P3r: không có mã nguồn
│   ├── coverage-guided/        # P4, P4w
│   ├── coverage-guided-reduced/
│   ├── mutation-feedback/      # P4b, P4bw
│   └── mutation-feedback-reduced/
├── experiments/
│   ├── config/                 # Cấu hình: model, nhiệt độ, số vòng lặp, ngân sách token, seed
│   ├── scripts/                # Script chạy và tái lập toàn bộ thực nghiệm
│   └── spikes/                 # Thử nghiệm kỹ thuật S1–S3 và kết quả
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
| LLM thương mại qua CLI | Sinh test: Codex CLI `gpt-6.2-astra` (chuỗi chính, ngữ cảnh hạn chế), Antigravity CLI `gemini-3.8-flash-low` (chuỗi LLM yếu hơn) |
| Python 3.12 (`scipy`, `matplotlib`, …) | Điều phối, đo, thống kê (Mann–Whitney U, Â12) |

## Cài đặt và chạy

> Tiến độ phát triển và các quyết định kỹ thuật: [docs/orchestration/progress.md](docs/orchestration/progress.md), [docs/orchestration/decisions.md](docs/orchestration/decisions.md). Báo cáo cuối của đợt phát triển tự động: [docs/orchestration/bao-cao-cuoi.md](docs/orchestration/bao-cao-cuoi.md).

Môi trường đã dùng: JDK 17, Maven 3.9, Python 3.12 với các gói trong `requirements.txt`:

```bash
python -m pip install -r requirements.txt
```

LLM mặc định của thực nghiệm là Codex CLI (`llm.provider: codex-cli` trong `experiments/config/default.yaml`), dùng phiên đăng nhập sẵn có của `codex`. Nếu đổi sang provider `anthropic`, đặt khóa trong file `.env` ở thư mục gốc (đã nằm trong `.gitignore`, **không commit**):

```
ANTHROPIC_API_KEY=...
```

Các lệnh chính (chạy từ gốc repo; `python` là Python của môi trường đã cài `requirements.txt`):

| Việc | Lệnh |
|---|---|
| Test cho công cụ | `python -m pytest tools/tests -q` |
| Chạy một bộ test trên subject (Maven) | `mvn -B -f subject/pom.xml clean test -Psuite -Dsuite.dir=<đường dẫn tuyệt đối tới bộ test>` |
| Đo một bộ test (coverage + mutation) | `python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --mutation` |
| Kiểm tra bộ test trên chương trình tham chiếu | `python experiments/scripts/measure.py --suite <bộ test> --method <mã> --run <n> --program reference` |
| Sinh P2 / P3 / P4 / P4b | `python tools/generate_p2.py --run N` · `python tools/generate_p3.py --run N [--variant spec-only\|weak-llm]` · `python tools/generate_p4.py --run N [--mutation-feedback] [--variant reduced\|weak-llm]` |
| Chạy lại toàn bộ thực nghiệm (tiếp tục được sau khi hết quota) | `python experiments/scripts/run_experiment.py --runs 1-10 --skip-existing` (xem `--dry-run`) |
| Thống kê, bảng, biểu đồ | `python experiments/scripts/analyze.py` → `results/comparison/` |
| Liệt kê mutant còn sống để rà soát tương đương | `python tools/mutation_ids.py --suite-results results/mutation/<mã>/run-XX` |
| Demo 10 bước | `python experiments/scripts/demo.py` (`--offline` không gọi LLM, `--no-pause` chạy liền) – kịch bản: [docs/demo/kich-ban-demo.md](docs/demo/kich-ban-demo.md) |

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

Chi tiết 5 gói việc, checklist và bảng đối chiếu yêu cầu của đề: [docs/ke-hoach-thuc-hien.md](docs/ke-hoach-thuc-hien.md).

| Mốc | Hạn | Nội dung |
|---|---|---|
| M1 | 25/11 | Chốt đặc tả và chữ ký hàm |
| M2 | 29/11 | Chương trình chạy được, đo được P1; có API key |
| M3 | 06/12 | P2, P3 chạy qua script đo chung |
| M4 | 13/12 | P4, phản hồi mutation, EvoSuite |
| M5 | 20/12 | Chạy đủ 10 lần, chốt số liệu |
| M6 | 27/12 | Báo cáo, slide, tập demo |
| Nộp | 31/12 | Rà soát, tag `v1.0-final` |
