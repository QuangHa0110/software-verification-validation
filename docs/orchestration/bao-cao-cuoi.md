# Báo cáo cuối đợt phát triển tự động (23–24/11/2026)

Orchestrator: Claude Code. Worker: Codex CLI (`gpt-6.2-astra`), Antigravity CLI (agy), một subagent Claude (T17, khi cả hai hết quota). Chi tiết từng bước: [progress.md](progress.md); quyết định: [decisions.md](decisions.md) (D-001 … D-027); brief giao việc: [tasks/](tasks/).

Nhãn: **[ĐÃ CHẠY]** = orchestrator đã chạy lệnh và thấy output; **[CHƯA CHẠY]** = đã viết, chưa có lệnh kiểm tra; **[GIẢ ĐỊNH]** = chưa kiểm chứng.

## 1. Trạng thái

| Mốc | Trạng thái |
|---|---|
| M1 đặc tả, chữ ký hàm | Xong, tag `m1-spec-frozen` |
| M2 chương trình + P1 đo được | Xong. [ĐÃ CHẠY] P1 181/181 xanh trên `subject` và `reference` |
| M3 P2, P3 | Xong |
| M4 P4, P4b (EvoSuite bỏ, D-013) | Xong |
| M5 chạy đủ, chốt số liệu | Xong, tag `m5-data-frozen`. [ĐÃ CHẠY] 11 phương pháp, 101 lần đo |
| M6 báo cáo, slide, demo | Bản nháp đầy đủ: báo cáo 6 chương (không còn mốc `[CHỜ]`), slide Marp 17 trang, demo 10 bước. [ĐÃ CHẠY] demo offline và demo với LLM thật |
| Nộp | **Chưa**: cần nhóm rà soát (mục 4) |

Kiểm tra cuối trên `main` [ĐÃ CHẠY]: `python -m pytest tools/tests -q` → `147 passed`. 111 commit trên `main`; `git log --format=%B main | grep -ciE "co-authored-by|generated with claude|claude-session"` → `0`.

## 2. Kết quả thực nghiệm chính

Nguồn: [results/comparison/summary.md](../../results/comparison/summary.md), [pairwise.md](../../results/comparison/pairwise.md). [ĐÃ CHẠY] `analyze.py`.

| Phương pháp | n | Branch TB | Mutation score TB ± SD |
|---|---:|---:|---:|
| P1 viết tay | 1 | 100,00% | 99,17% |
| P2 ngẫu nhiên | 10 | 76,78% | 64,33 ± 4,41% |
| P3 / P4 / P4b (đủ ngữ cảnh) | 10 mỗi | 100,00% | 100,00 ± 0 |
| P3r / P4r / P4br (chỉ đặc tả) | 10 mỗi | 100,00% | 100,00 ± 0 |
| P3w (LLM yếu hơn) | 10 | 99,66% | 95,00 ± 2,66% |
| P4w (+ phản hồi coverage) | 10 | 99,94% | 95,00 ± 2,66% |
| P4bw (+ phản hồi mutation) | 10 | 99,94% | 98,50 ± 0,35% |

- Hiệu ứng trần ở hai chuỗi dùng `gpt-6.2-astra`, kể cả khi bỏ mã nguồn khỏi prompt (D-024).
- Chuỗi LLM yếu hơn: phản hồi coverage tăng nhánh ở 2/10 lần nhưng không tăng mutant bị diệt; phản hồi mutation tăng mutant bị diệt ở 10/10 lần (P4bw vs P4w: U = 100, p = 0,0001, Â12 = 1,0), đổi lại thời gian sinh tích lũy trung vị tăng từ 213,7 s lên 978,9 s.
- Mutant tương đương: 1/121 (`PricingService:24`, index 85), rà soát hai đợt: [equivalent-review.md](../../results/mutation/equivalent-review.md).

## 3. Đối chiếu yêu cầu của đề (kế hoạch mục 7)

| Yêu cầu | Bằng chứng |
|---|---|
| NV1: ≥ 5 công trình, hạn chế, vấn đề | `docs/literature/` (17 công trình, có DOI), báo cáo Chương 2 |
| NV2: chương trình, đặc tả, bộ test chuẩn | `subject/` (14 lớp), `docs/spec/`, `reference/`, P1 181 ca |
| NV3: ≥ 1 LLM, ≥ 2 chiến lược, prompt, test sinh tự động, phản hồi coverage, script tái lập | 2 LLM; P3, P4, P4b (+ 2 thí nghiệm bóc tách); `prompts/` (6 bộ); `test-suites/`; `tools/`, `experiments/scripts/run_experiment.py` |
| NV4: statement/branch/function coverage, nhánh chưa phủ, nguyên nhân, trước/sau | `results/coverage/`, `coverage-before-after*.md`, Chương 5.2 |
| NV5: 5 toán tử, mutant tương đương, score từng phương pháp | D-012, D-016 (PIT + mutant tự tạo), `results/mutation/`, `equivalent-mutants.csv`, Chương 5.3 |
| NV6: so sánh cùng điều kiện, nhiều lần chạy, ghi cấu hình LLM, token | 10 lần mỗi phương pháp sinh ngẫu nhiên; `results/llm-logs/` (70 lời gọi); `experiments/config/default.yaml` |
| RQ1–RQ6 bằng số liệu | Chương 5.5 |
| Báo cáo 6 chương | `docs/report/` |
| Demo 10 bước, chạy lại được | `docs/demo/kich-ban-demo.md`, `experiments/scripts/demo.py` |
| Chứng minh oracle đúng | `reference/` viết độc lập (agy/Gemini, không xem `subject/`); bộ lọc 4 tầng; tỷ lệ oracle đúng 100% ở mọi lần sinh bằng LLM |
| Cải tiến phương pháp | Phản hồi mutation (P4b), chọn test theo ma trận diệt (D-026), kiểm tra oracle bằng chương trình tham chiếu |
| Không mặc định LLM tốt hơn | Chương 5.5 (RQ6: P2 nhanh hơn, không tốn token), 5.6, 6.2 |

## 4. Việc nhóm cần làm (không tự quyết được thay nhóm)

1. **Rà soát và xác nhận** các quyết định orchestrator đã tự chọn (D-003: quy tắc nghiệp vụ, Python, thiết kế ghép cặp; D-024, D-025: hai thí nghiệm bổ sung sau khi thấy trần). Đảo quyết định nào thì ghi mục mới trong `decisions.md`.
2. **Phân công thành viên** (`docs/ke-hoach-thuc-hien.md` mục 1 vẫn trống) và ghi tên/mã học viên trong đề xuất.
3. **Đọc duyệt báo cáo** 6 chương; kiểm lại trích dẫn trong `docs/literature/` (orchestrator chỉ kiểm chéo một phần, xem progress.md).
4. **Push lên GitHub**: mọi commit đang ở máy cục bộ (D-007). Nhánh làm việc đã gộp vào `main`; có thể xóa các nhánh `feature/*` đã gộp sau khi push.
5. Render slide (`docs/demo/slides.md`, Marp) và tập dượt demo (`python experiments/scripts/demo.py`, có `--offline` dự phòng).
6. Tùy chọn: chạy EvoSuite thủ công nếu muốn thêm đối chứng (D-013).
7. Gắn tag `v1.0-final` khi nộp.

## 5. Giới hạn của chính quy trình phát triển

- Codex hết quota 3 lần, agy hết quota Gemini 1 lần; agy headless thường bị từ chối quyền lệnh nên không dùng cho việc viết mã từ D-027.
- Một lệnh giao việc (T13, EvoSuite) và một lệnh chạy codex không sandbox bị chính sách quyền chặn; orchestrator không tìm cách vượt qua.
- Máy người dùng tắt khoảng 16:10–21:50 ngày 23/11; thời gian đo của các lần chạy trước và sau có thể chịu tải hệ thống khác nhau (ghi ở Chương 5.6).
