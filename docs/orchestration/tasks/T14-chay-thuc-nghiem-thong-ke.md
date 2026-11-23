# T14 – `run_experiment.py` và `analyze.py`

**Worker:** codex, effort `high` · **Nhánh:** `experiment/runner` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 2, 5, 6, 8 (T14), 9; `docs/de-xuat-de-tai-1.md` mục 6, 8 (RQ1–RQ6, tiêu chí); `experiments/scripts/measure.py`; `tools/generate_p2.py`, `tools/generate_p3.py`, `tools/generate_p4.py`; `experiments/config/default.yaml`.

## Phạm vi

- `experiments/scripts/run_experiment.py`, `experiments/scripts/analyze.py`
- `tools/svv/stats.py` (Mann–Whitney U, Vargha–Delaney Â12, thống kê mô tả)
- `tools/tests/test_stats.py`, `tools/tests/test_run_experiment.py`, `tools/tests/test_analyze.py`

**Không** chạy toàn bộ thực nghiệm trong bước này (orchestrator chạy sau khi review). Chỉ chạy phần kiểm tra ở cuối.

## `run_experiment.py`

```
python experiments/scripts/run_experiment.py [--methods p1-manual,p2-random,p3-direct-llm,p4-coverage-guided,p4b-mutation-feedback,evosuite] [--runs 1-10] [--skip-generation] [--skip-existing] [--dry-run]
```

- Với mỗi phương pháp và mỗi `run`: sinh bộ test nếu chưa có hoặc không có `--skip-generation` (P1 chỉ `run-01`, không sinh); sau đó đo bằng `measure.py --mutation`. Thứ tự bắt buộc theo thiết kế ghép cặp: P3 `run-XX` → P4 `run-XX` → P4b `run-XX`.
- `--skip-existing`: bỏ qua bước đã có kết quả (bộ test có `generation.json`; `results/comparison/raw/<m>/run-XX.json`). Chạy lại được sau khi bị ngắt.
- Gặp `LlmQuotaExceeded`: dừng sạch, in rõ phương pháp/lần chạy đang dở và lệnh để chạy tiếp; exit mã riêng (ví dụ 3).
- Ghi nhật ký tiến trình `results/comparison/run-log.jsonl` (mỗi bước một dòng: thời điểm, phương pháp, run, bước, kết quả, thời gian).
- `--dry-run`: chỉ in kế hoạch các bước.
- Phương pháp không có công cụ sinh (ví dụ `evosuite` khi S4 không đạt): bỏ qua, in lý do.

## `analyze.py`

Đọc `results/comparison/raw/**/run-XX.json`, ghi vào `results/comparison/`:

1. `summary.csv` và `summary.md`: mỗi phương pháp × mỗi tiêu chí: n, trung bình, độ lệch chuẩn, trung vị, min, max. Tiêu chí (8 tiêu chí của đề cộng các tỷ lệ của thiết kế 4.2): statement/line %, branch %, function %, mutation score, số test, thời gian thực thi, thời gian sinh, chi phí (token vào, token ra, `cost_usd` nếu có), tỷ lệ biên dịch được, tỷ lệ oracle đúng.
2. `pairwise.csv` và `pairwise.md`: các cặp phục vụ RQ: P4 vs P3 (RQ2, RQ4), P3 vs P2 và P4 vs P2 (RQ3), P4b vs P4 (cải tiến), mỗi tiêu chí: Mann–Whitney U (hai phía), p-value, Â12 và mức hiệu ứng (ngưỡng Vargha–Delaney: 0,56 / 0,64 / 0,71). Phương pháp chỉ có một lần chạy (P1) không đưa vào kiểm định, chỉ đưa vào bảng mô tả. **Không** tự diễn giải "tốt hơn" trong file; chỉ ghi số.
3. `coverage-before-after.csv/.md`: theo cặp `run-XX`, coverage P3 (trước) và P4 (sau), cộng mutation P4 → P4b.
4. `iterations.csv`: gộp `iterations` của P4/P4b.
5. `rq5-correlation.csv`: hệ số Spearman giữa số test, coverage và mutation score (gộp mọi lần chạy, ghi rõ n).
6. Biểu đồ PNG trong `results/comparison/figures/` (matplotlib, backend `Agg`): boxplot coverage, boxplot mutation score theo phương pháp; đường coverage qua các vòng của P4; scatter số test và mutation score. Mỗi hình có tiêu đề, nhãn trục, đơn vị.

Mọi file văn bản ghi UTF-8. Số liệu lấy **chỉ** từ file kết quả; thiếu dữ liệu thì ghi `n = 0`, không suy diễn.

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` xanh, gồm: Â12 với ví dụ tính tay (ví dụ [1,2,3] vs [2,3,4]); Mann–Whitney so với `scipy.stats.mannwhitneyu`; `analyze.py` trên một thư mục kết quả giả nhỏ (fixture) sinh đủ các file; `run_experiment.py --dry-run` in kế hoạch đúng thứ tự ghép cặp.
- `python experiments/scripts/run_experiment.py --dry-run` → in kế hoạch.
- `python experiments/scripts/run_experiment.py --methods p1-manual --runs 1` → chạy thật P1, rồi `python experiments/scripts/analyze.py` → báo cáo nội dung `summary.md` đúng như output. Sau đó xóa `results/comparison/*`, `results/coverage/p1-manual`, `results/mutation/p1-manual` (orchestrator sẽ chạy chính thức).
