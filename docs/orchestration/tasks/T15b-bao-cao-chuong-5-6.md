# T15b – Báo cáo khoa học: Chương 5–6 (kết quả, thảo luận, kết luận)

**Worker:** codex hoặc agy, effort cao · **Nhánh:** `docs/report-ch56` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/report/` (chương 1–4 đã có), `docs/orchestration/decisions.md` (đặc biệt D-012, D-016, D-020, D-024, D-025), `docs/orchestration/progress.md`, `results/comparison/` (**sinh lại trước khi viết**, xem dưới), `results/mutation/equivalent-review.md`, `results/mutation/equivalent-mutants.csv`, `test-suites/*/run-XX/generation.json`.

## Bước 0 – Sinh số liệu

Chạy `python experiments/scripts/analyze.py` (ghi vào `results/comparison/`). Mọi con số trong chương 5 phải **trích từ các file do lệnh này sinh** (`summary.md`, `pairwise.md`, `coverage-before-after.md`, `iterations.csv`, `rq5-correlation.csv`, `figures/`) hoặc từ `results/comparison/raw/**`. Ghi nguồn (tên file) cạnh mỗi bảng.

## Phạm vi

- `docs/report/chuong-5-ket-qua-va-thao-luan.md`, `docs/report/chuong-6-ket-luan.md`, `docs/report/README.md` (cập nhật trạng thái)
- Được commit các file do `analyze.py` sinh trong `results/comparison/` (orchestrator sẽ commit).
- Không sửa file khác.

## Cấu trúc

**Chương 5.** 5.1 Kết quả sinh test (số test, tỷ lệ biên dịch, tỷ lệ oracle đúng, token, thời gian cho từng phương pháp); 5.2 Coverage (bảng theo phương pháp, nhánh chưa phủ còn lại và **phân tích nguyên nhân**, bảng trước/sau P3→P4, P3r→P4r, P3w→P4w); 5.3 Mutation (score từng phương pháp, theo 5 toán tử, mutant tương đương đã loại và lý do, mutant còn sống và nguyên nhân); 5.4 So sánh các phương pháp (Mann–Whitney U, Â12 từ `pairwise.md`; nêu rõ khi phân bố hằng thì kiểm định không có ý nghĩa); 5.5 Trả lời RQ1–RQ6, mỗi RQ một tiểu mục, **dẫn số liệu**; 5.6 Threats to Validity (nội tại, ngoại tại, cấu trúc, kết luận), bắt buộc nêu: hiệu ứng trần trên chương trình 235 dòng; đặc tả rất chi tiết (`branches.md`) gần như là danh sách ca kiểm thử; LLM qua CLI có token nền; một chương trình đối tượng duy nhất; số lần chạy của từng chuỗi (đúng số thực tế); nhiễu thời gian do chạy trên máy cá nhân; EvoSuite không thực hiện (D-013); toán tử hằng boolean chỉ 2 mutant (D-016).

**Chương 6.** 6.1 Kết luận (trả lời câu hỏi trọng tâm, không phóng đại); 6.2 Hạn chế; 6.3 Hướng phát triển.

## Quy tắc

- **Không** bịa, làm tròn cho đẹp, hay suy diễn số không có trong file. Số lần chạy của mỗi phương pháp lấy đúng `n` trong `summary.md`.
- Nêu thẳng cả những tiêu chí LLM **không** hơn phương pháp khác (ví dụ chi phí, thời gian) và các trường hợp phản hồi không cải thiện.
- Kết luận về P4/P4b phải tách theo ba chuỗi: đủ ngữ cảnh (trần), ngữ cảnh hạn chế (trần), LLM yếu hơn.
- Liệt kê trong báo cáo cuối mọi chỗ còn `[CHỜ]`.
