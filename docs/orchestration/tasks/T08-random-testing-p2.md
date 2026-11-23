# T08 – P2 Random Testing: bộ sinh test ngẫu nhiên

**Worker:** codex, effort `medium` · **Nhánh:** `feature/random-testing` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/spec/`, `docs/thiet-ke-ky-thuat.md` mục 2, 6 (dòng P2), 9; `experiments/config/default.yaml` (khóa `random`).

## Ràng buộc

- Expected output **chỉ được tính bằng cách gọi `reference/`**, không bao giờ từ `subject/` (thiết kế 6). Bộ sinh **không** đọc mã nguồn `subject/`.
- Đầu vào ngẫu nhiên có seed cố định: seed của `run-XX` = `random.seed_base + XX`. Cùng seed phải ra **cùng nội dung file** (byte-for-byte).

## Phạm vi

- `tools/random-gen/pom.xml`: Maven, phụ thuộc vào artifact của `reference/` (cài bằng `mvn -f reference/pom.xml install -DskipTests` vào local repository, hoặc thêm `reference/target/classes` vào classpath; chọn cách đơn giản, ghi lại).
- `tools/random-gen/src/main/java/**`: chương trình sinh test.
- `tools/generate_p2.py`: chạy bộ sinh cho một `run`, ghi `test-suites/p2-random/run-XX/*.java` và `generation.json` (thiết kế 6; `llm.calls = 0`).
- `tools/tests/test_generate_p2.py`.

## Yêu cầu

1. Với mỗi hàm public trong `docs/spec/api.md`, sinh đầu vào ngẫu nhiên theo **miền rộng có ràng buộc kiểu** (ví dụ số lượng trong khoảng `[-5, 60]`, khối lượng `[-100, 25000]`, ngày trong khoảng ±60 ngày quanh hạn của mã), để có cả đầu vào hợp lệ và không hợp lệ. Không cố ý nhắm vào giá trị biên: đây là đối chứng ngẫu nhiên.
2. Gọi `reference/` để lấy kết quả. Kết quả là giá trị → sinh `assertEquals`; ném ngoại lệ → sinh `assertThrows(<đúng kiểu>.class, ...)`. Với hàm có trạng thái (giỏ, đơn hàng), sinh **chuỗi thao tác ngẫu nhiên ngắn** rồi assert trạng thái quan sát được (subtotal, số lượng, tồn kho, trạng thái đơn, tiền hoàn).
3. Tổng số test mỗi lần chạy = `random.num_tests` (cấu hình). Mỗi test là một phương thức `@Test` với **giá trị cụ thể** (không gọi `Random` lúc chạy test). Package `shop`, lớp kết thúc bằng `Test`.
4. Ghi thời gian sinh vào `generation.json`.

## Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python tools/generate_p2.py --run 1` hai lần → so sánh hai lần sinh giống hệt nhau (hash file).
- Sinh `run-01` … `run-10`; mỗi bộ biên dịch được, và **xanh trên `reference/`**: `python experiments/scripts/measure.py --suite test-suites/p2-random/run-01 --method p2-random --run 1 --program reference`.
- Chạy đo trên `subject/` cho `run-01`: `python experiments/scripts/measure.py --suite test-suites/p2-random/run-01 --method p2-random --run 1 --mutation`. Báo cáo exit code và các số coverage **đúng như output**. Nếu fail trên subject thì báo đúng test nào fail; **không sửa test hay subject**.
- `python -m pytest tools/tests -q` xanh.
- Sau khi kiểm tra, xóa kết quả đo trong `results/` (đo chính thức làm ở T14), **giữ** các bộ `test-suites/p2-random/run-XX/`.
