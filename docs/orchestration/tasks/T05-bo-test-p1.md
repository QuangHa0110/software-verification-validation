# T05 – Bộ test P1 (Manual Testing, viết theo đặc tả)

**Worker:** codex, effort `high` · **Nhánh:** `feature/manual-tests` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), toàn bộ `docs/spec/` (đặc biệt `branches.md`), `docs/thiet-ke-ky-thuat.md` mục 3.7 và 9.

## Ràng buộc độc lập

P1 mô phỏng việc tester viết test **chỉ từ đặc tả, không xem mã**. Nó đồng thời là bộ test chuẩn để kiểm tra `subject/` và `reference/`.

- **Chỉ** đọc `docs/` và khung chữ ký trong `subject/src/main/java/shop/` của worktree này (thân hàm là `UnsupportedOperationException`).
- **Không** đọc bất kỳ thư mục nào ngoài worktree, không xem nhánh khác hay lịch sử git.

## Phạm vi

- `test-suites/p1-manual/run-01/*.java` (package `shop`, tên lớp kết thúc bằng `Test`, JUnit 5). Xóa `test-suites/p1-manual/.gitkeep`.
- `test-suites/p1-manual/run-01/README.md`: bảng truy vết test → mục đặc tả / dòng trong `branches.md`.
- `docs/spec/open-questions.md`: chỉ được **thêm** dòng (ghi "từ T05").

## Việc cần làm

1. Viết **ít nhất 60 test** (`@Test`, có thể dùng `@ParameterizedTest`; mỗi bộ tham số tính là một test). Kỹ thuật: phân hoạch tương đương, giá trị biên, bảng quyết định, chuyển trạng thái đơn hàng.
2. Phủ **mọi dòng của `docs/spec/branches.md`** và **mọi nhánh khó phủ** của thiết kế 3.7: miễn phí vận chuyển dựa trên giá trị sau giảm giá; mã `PERCENT` chạm trần và đúng ngưỡng `minOrder`; ngày hết hạn đúng bằng hôm nay; biên khối lượng 1000/1001/1500/1501/20000/20001; biên 24 giờ khi hủy (đúng 24 giờ, 24 giờ 1 giây); tính nguyên tử của `placeOrder` khi vận chuyển lỗi; thứ tự kiểm tra trong `addItem` và `updateQuantity` (đầu vào vi phạm nhiều điều kiện cùng lúc).
3. Oracle: giá trị mong đợi **tính tay từ đặc tả**, ghi phép tính ngắn trong chú thích ngay trên assert. Kiểm tra ngoại lệ bằng `assertThrows` với **đúng kiểu ngoại lệ**, không so sánh message. Kiểm tra cả trạng thái không đổi sau ngoại lệ (tồn kho, giỏ hàng, trạng thái đơn).
4. Thời gian: dùng hằng `LocalDate` / `LocalDateTime` cố định, không gọi `now()`.
5. Mỗi test độc lập: tự tạo dữ liệu, không dùng trạng thái `static` có thể sửa.
6. Kiểm tra biên dịch với khung: `javac --release 17 -encoding UTF-8` với classpath gồm junit-jupiter-api (và junit-jupiter-params nếu dùng) lấy từ local Maven repository trong `MAVEN_OPTS`. Nếu chưa có jar, chạy `mvn -B -f subject/pom.xml test-compile` một lần để Maven tải về. Test sẽ **fail** trên khung vì thân hàm chưa cài; như vậy là đúng, chỉ cần biên dịch được.

## Tiêu chí xong

- Lệnh biên dịch exit 0 (ghi lệnh và output).
- Đếm số test (số `@Test` cộng số bộ tham số), nêu trong báo cáo.
- README truy vết phủ hết các dòng của `branches.md`; dòng nào không phủ được thì ghi lý do.
