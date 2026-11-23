# T21 – Tối ưu bước chọn theo mutation (ma trận diệt theo từng test)

**Worker:** codex, effort `high` · **Nhánh:** `feature/kill-matrix` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/orchestration/decisions.md` (**D-026**), `tools/generate_p4.py`, `tools/svv/selection.py`, `tools/svv/pit.py`, `tools/svv/custom_mutation.py`, `experiments/scripts/measure.py`, `subject/pom.xml`.

## Vấn đề

Trong P4b (và các biến thể P4br, P4bw), bước chọn tham lam hiện đo mutation đầy đủ cho nhiều tổ hợp ứng viên (k(k+1)/2 lần với k ứng viên). Mỗi lần đo gồm PIT và 37 mutant tự tạo, mất 1,5–2 phút. Với 12 ứng viên, một vòng mất vài giờ (quan sát thật ngày 24/11).

## Yêu cầu

1. **Một lần đo cho mỗi vòng**: chạy mutation **một lần** trên bộ hiện tại cộng **mọi** ứng viên hợp lệ, lấy ma trận diệt:
   - PIT: bật `fullMutationMatrix=true` (property Maven, chỉ bật khi cần, mặc định tắt để không đổi các phép đo chính thức), đọc `killingTests` (và `succeedingTests` nếu cần) trong `mutations.xml`; ánh xạ tên test về lớp/phương thức test (bộ lọc đặt mỗi khối vào lớp `shop.<Prefix>_NNNTest`).
   - Mutant tự tạo: khi chạy bộ test trên một mutant, ghi lại **tên các test fail** (đọc surefire), không chỉ trạng thái KILLED.
2. **Chọn tham lam phủ tập hợp** trong `selection.py`: tập mutant bị bộ hiện tại diệt là `K0`; mỗi ứng viên `c` có tập `kill(c)`; lặp chọn ứng viên có `|kill(c) \ K|` lớn nhất (hòa thì theo thứ tự sinh), cập nhật `K`, dừng khi không còn ứng viên đóng góp > 0. Mutant tương đương (theo `equivalent-mutants.csv`) không tính.
3. Kết quả chọn phải **tương đương ngữ nghĩa** với cách cũ (giữ test diệt thêm mutant). Viết test so sánh hai cách trên dữ liệu giả lập nhỏ cho cùng kết quả.
4. Sau khi chọn, đo lại **một lần** bộ đã gộp (để số liệu iterations dựa trên phép đo thật, không suy từ ma trận).
5. Ghi vào `iterations` thêm `measure_runs` (số lần chạy Maven của vòng) để báo cáo chi phí.
6. Không đổi cách đo chính thức của `measure.py` (vẫn tắt `fullMutationMatrix`).

## Kiểm tra

- `python -m pytest tools/tests -q` xanh, gồm test Maven thật nhỏ: bộ hiện tại + 3 ứng viên (1 diệt thêm 2 mutant, 1 diệt thêm 1 mutant trùng, 1 không diệt thêm) → chỉ chọn ứng viên đầu, `measure_runs` ≤ 2.
- Ghi lệnh để orchestrator chạy lại P4bw run-01 thật và thời gian kỳ vọng.
