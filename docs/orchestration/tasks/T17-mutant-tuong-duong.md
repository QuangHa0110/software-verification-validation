# T17 – Định danh mutant tương đương chính xác và áp dụng lúc phân tích

**Worker:** codex, effort `medium` · **Nhánh:** `feature/equivalent-mutants` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 5.3, 5.4; `tools/svv/pit.py`, `tools/svv/custom_mutation.py`, `experiments/scripts/measure.py`, `experiments/scripts/analyze.py`, `results/mutation/equivalent-mutants.csv`.

## Vấn đề (orchestrator đã quan sát)

Trong `mutations.xml` của P1, tại `PricingService.java` dòng 24 (`if (coupon.getMaxDiscount() > 0 && discount > coupon.getMaxDiscount())`) PIT sinh **hai** mutant `ConditionalsBoundaryMutator` cùng mô tả `changed conditional boundary`: một cho `> 0`, một cho `discount > max`. Chỉ mutant thứ hai tương đương. Khóa hiện tại (`class, method, line, mutator, description`) không phân biệt được hai mutant này.

## Phạm vi

- `tools/svv/pit.py`, `tools/svv/custom_mutation.py` (chỉ phần định danh)
- `experiments/scripts/measure.py`, `experiments/scripts/analyze.py`
- `tools/mutation_ids.py` (CLI mới)
- `results/mutation/equivalent-mutants.csv` (**chỉ** đổi dòng tiêu đề, không thêm dữ liệu)
- `tools/tests/**`

## Yêu cầu

1. Thêm cột `index` vào `equivalent-mutants.csv`: với mutant PIT là giá trị của `<indexes><index>` (nối bằng `;` nếu nhiều) cùng `<blocks>` nếu cần để duy nhất; với mutant tự tạo là `id` ổn định đã có. Tiêu đề mới: `source,class,method,line,mutator,index,description,reason`. Khớp khi mọi cột trừ `reason` bằng nhau; cột `index` rỗng thì khớp mọi index (tương thích ngược).
2. `tools/mutation_ids.py --suite-results results/mutation/<method>/run-XX` in bảng mọi mutant **không bị diệt** (`SURVIVED`, `NO_COVERAGE`) với đủ các cột khóa và đoạn mã nguồn của dòng đó, để người rà soát chép dòng vào CSV.
3. Lưu trong `run-XX.json` danh sách mutant không bị diệt kèm khóa đầy đủ (trường mới `mutation.alive`, cập nhật schema), để `analyze.py` **tính lại** score với `equivalent-mutants.csv` hiện hành mà **không cần đo lại**. `analyze.py` dùng score tính lại này và ghi rõ trong `summary.md` số mutant tương đương đã loại.
4. Viết công cụ chuyển đổi một lần (hoặc trong `analyze.py`) để các `run-XX.json` cũ (thiếu `alive`) được bổ sung từ `results/mutation/<method>/run-XX/mutations.xml` và `custom-mutations.json` đã lưu.

## Kiểm tra (chạy thật)

- `python -m pytest tools/tests -q` xanh, có test hai mutant cùng dòng cùng mutator khác `index`: chỉ mutant được đánh dấu bị loại.
- `python tools/mutation_ids.py --suite-results results/mutation/p1-manual/run-01` (nếu thư mục có trong worktree) → báo cáo output.
- Không thêm dòng dữ liệu nào vào `equivalent-mutants.csv` (orchestrator rà soát và điền).
