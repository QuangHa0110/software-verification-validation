# T07 – Mutant tự tạo ở mức mã nguồn (`mutate_custom.py`)

**Worker:** codex, effort `high` · **Nhánh:** `feature/measurement` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/orchestration/decisions.md` (D-012), `experiments/spikes/s1-s2/KET-QUA.md`, `docs/thiet-ke-ky-thuat.md` mục 5.3, 5.4; `tools/svv/` (đặc biệt `pit.py`, `workdir.py`, `maven.py`, `surefire.py`); `experiments/scripts/measure.py`.

## Phạm vi

- `tools/mutate_custom.py` (CLI) và `tools/svv/custom_mutation.py` (thư viện)
- `tools/svv/pit.py`, `experiments/scripts/measure.py`: chỉ phần gộp kết quả mutant tự tạo
- `experiments/config/default.yaml`: chỉ thêm khóa mới (ví dụ `measure.custom_mutation: true`, timeout cho mỗi mutant)
- `tools/tests/test_custom_mutation.py`, `tools/tests/fixtures/**`

## Toán tử (theo D-012)

| Khóa `by_operator` | Biến đổi | Vị trí đủ điều kiện |
|---|---|---|
| `conditional` | `&&` → `\|\|` và `\|\|` → `&&` | mỗi lần xuất hiện của toán tử, ngoài chuỗi và chú thích |
| `boolean_constant` | `true` → `false`, `false` → `true` | literal boolean, ngoài chuỗi và chú thích |
| `statement_deletion` | xóa cả câu lệnh | câu lệnh biểu thức là gán biến cục bộ (`x = ...;`), gán phức hợp (`x += ...;`), `x++;`/`x--;`/`++x;`/`--x;`. **Không** xóa: khai báo biến, `return`, `throw`, `break`/`continue`, lời gọi phương thức `void`, gán trường (`this.x = ...`, `x = ...` với `x` là trường); hai loại cuối PIT đã làm |

Phân tích mã: không cần trình phân tích Java đầy đủ. Dùng tokenizer tự viết đủ để bỏ qua chuỗi, ký tự, chú thích và nhận diện câu lệnh trên một dòng. Phân biệt trường và biến cục bộ bằng cách thu thập tên trường khai báo ở cấp lớp. Ghi rõ giới hạn trong docstring.

## Quy trình

1. Liệt kê mọi mutant của `subject/src/main/java/shop/*.java`: `id`, `file`, `class`, `method` (phương thức bao quanh), `line`, `operator`, `original`, `mutated`, `description`. Danh sách phải **ổn định** (cùng mã → cùng thứ tự, cùng id).
2. Với mỗi mutant: tạo bản sao chương trình trong `.work/`, áp dụng biến đổi, biên dịch. Biên dịch lỗi → trạng thái `COMPILE_ERROR`, **loại khỏi mẫu số** (mutant không hợp lệ), ghi lại.
3. Chạy bộ test trên mutant (timeout = `timeout_factor` × thời gian chạy trên bản gốc + hằng số trong cấu hình). Có test fail/error → `KILLED`; timeout → `TIMED_OUT` (tính là killed, như PIT); xanh → `SURVIVED`. Mutant ở dòng không được bộ test phủ (theo `jacoco.xml` của bản gốc) → `NO_COVERAGE`, **không cần chạy**, vẫn nằm trong mẫu số.
4. Có thể chạy song song (số luồng trong cấu hình). Kết quả ghi ra `custom-mutations.json` cạnh `mutations.xml` của PIT trong `results/mutation/<method>/run-XX/`.
5. `measure.py --mutation`: chạy PIT, rồi chạy mutant tự tạo (nếu `measure.custom_mutation` bật), gộp hai nguồn vào `mutation` và `by_operator` của `run-XX.json`, áp dụng `equivalent-mutants.csv` cho cả hai nguồn (`source` = `pit` hoặc `custom`).

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` xanh, gồm test liệt kê mutant trên fixture (chuỗi chứa `&&`, chú thích chứa `true`, gán trường và gán cục bộ, `i++` trong `for` header **không** bị xóa), và test áp dụng biến đổi.
- `python tools/mutate_custom.py --list` trên `subject/`: in số mutant theo từng toán tử. Nêu các con số trong báo cáo.
- `python experiments/scripts/measure.py --suite test-suites/smoke --method smoke --run 1 --mutation`: exit 0; `by_operator` có đủ 5 khóa của đề, mỗi khóa `total > 0`. Báo cáo bảng `by_operator` đúng như output.
- Xóa kết quả đo của `smoke` trong `results/` sau khi kiểm tra.
