# T02 – Cài đặt chương trình đối tượng `subject/`

**Worker:** codex, effort `high` · **Nhánh:** `feature/subject-program` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), toàn bộ `docs/spec/`, `docs/thiet-ke-ky-thuat.md` mục 3 và 9.

## Phạm vi

- `subject/src/main/java/shop/*.java`: thay thân `UnsupportedOperationException` bằng cài đặt thật. **Không đổi chữ ký** đã có trong `docs/spec/api.md`.
- `subject/pom.xml`: **chỉ** thêm profile `suite` (mô tả bên dưới), không đổi phần khác.
- `test-suites/smoke/*.java`: test khói của người cài đặt.
- `docs/spec/open-questions.md`: chỉ được **thêm** dòng.

## Việc cần làm

1. Cài đặt đúng `docs/spec/`, kể cả **thứ tự kiểm tra** và quy tắc "ném ngoại lệ thì không đổi trạng thái".
2. Yêu cầu cấu trúc cho thực nghiệm (thiết kế 3.7, kế hoạch G1): có `if/else` lồng nhau, `switch` (phí theo khu vực), **vòng lặp** (phụ phí khối lượng, duyệt dòng hàng), xử lý ngoại lệ. Viết mã tự nhiên, rõ ràng, không cố tình thêm mã chết, không thêm nhánh ngoài đặc tả.
3. **Profile `suite`** trong `subject/pom.xml`: dùng `build-helper-maven-plugin` (ghim phiên bản) thêm thư mục test lấy từ property `suite.dir`, để chạy được một bộ test bất kỳ mà không chép vào `src/test/java`:
   `mvn -B -f subject/pom.xml test -Psuite -Dsuite.dir=<đường dẫn tuyệt đối tới thư mục bộ test>`.
   JaCoCo và PIT vẫn phải chạy được khi bật profile này. `subject/src/test/java/` vẫn để trống.
4. **Test khói** `test-suites/smoke/` (package `shop`, lớp tên kết thúc bằng `Test`): mỗi phương thức public có ít nhất một test đường đi thông thường, cộng một test cho mỗi loại ngoại lệ. Đây chỉ là kiểm tra nhanh của người cài đặt, không phải bộ P1.
5. Chạy `mvn -B -f subject/pom.xml test -Psuite -Dsuite.dir=<tuyệt đối>/test-suites/smoke` cho đến khi xanh. Sau đó chạy thêm `mvn -B -f subject/pom.xml org.pitest:pitest-maven:mutationCoverage -Psuite -Dsuite.dir=...` một lần để xác nhận PIT chạy được với profile (không cần điểm cao).

## Tiêu chí xong

- Lệnh test với bộ smoke: exit 0. Báo cáo số test, số lỗi, lấy từ output surefire.
- Có `subject/target/site/jacoco/jacoco.xml`; báo cáo % LINE và BRANCH lấy từ file đó (để tham khảo).
- PIT chạy xong và sinh `mutations.xml`.
- Không có chữ ký nào khác `api.md`.
