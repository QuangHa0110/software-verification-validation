# T03 – Cài đặt chương trình tham chiếu `reference/` (độc lập)

**Worker:** agy · **Nhánh:** `feature/reference` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), toàn bộ `docs/spec/`, `docs/thiet-ke-ky-thuat.md` mục 4 và 9.

## Ràng buộc độc lập (quan trọng nhất)

`reference/` là bản cài đặt thứ hai, dùng làm chuẩn "đúng theo đặc tả" để kiểm tra oracle của test. Nó chỉ có giá trị khi được viết **độc lập**:

- **Chỉ** đọc `docs/` và khung chữ ký trong `subject/src/main/java/shop/` của worktree này (thân hàm toàn là `UnsupportedOperationException`).
- **Không** đọc bất kỳ thư mục nào ngoài worktree của bạn, đặc biệt các thư mục `<WT_ROOT>\*` (thư mục chứa các worktree) khác và `<REPO_ROOT>` (bản checkout chính). Không chạy `git log -p`, `git show` hay xem nhánh khác.

## Phạm vi

- `reference/pom.xml`: sao chép `subject/pom.xml`, đổi `artifactId` thành `reference`, giữ nguyên plugin, phiên bản và profile `suite` (nếu chưa có profile `suite` thì thêm theo mô tả ở dưới).
- `reference/src/main/java/shop/*.java`: cài đặt **từ đặc tả**, cùng package, cùng tên lớp, cùng chữ ký như `docs/spec/api.md`.
- `test-suites/smoke-reference/*.java`: test khói của bạn.
- `docs/spec/open-questions.md`: chỉ được **thêm** dòng (ghi rõ "từ T03").

**Không** sửa `subject/` hay `docs/spec/` (trừ `open-questions.md`).

## Việc cần làm

1. Cài đặt đúng từng bước của `docs/spec/`, kể cả thứ tự kiểm tra và quy tắc "ném ngoại lệ thì không đổi trạng thái". Ưu tiên mã rõ ràng, bám sát câu chữ đặc tả. Không cần tối ưu.
2. Profile `suite`: `build-helper-maven-plugin` thêm thư mục test từ property `suite.dir`, chạy bằng `mvn -B -f reference/pom.xml test -Psuite -Dsuite.dir=<đường dẫn tuyệt đối>`.
3. Test khói: mỗi phương thức public ít nhất một test đường đi thông thường, cộng một test cho mỗi loại ngoại lệ, cộng **toàn bộ ví dụ số** có trong `docs/spec/`.
4. Chạy `mvn -B -f reference/pom.xml test -Psuite -Dsuite.dir=<tuyệt đối>/test-suites/smoke-reference` cho đến khi xanh.
5. Mọi chỗ đặc tả mơ hồ: ghi vào `open-questions.md` kèm cách bạn đã chọn.

## Tiêu chí xong

- Lệnh test trên: exit 0. Báo cáo số test và số lỗi lấy từ output surefire.
- Chữ ký trùng khớp `docs/spec/api.md` (tự đối chiếu, nêu kết quả trong báo cáo).
- Báo cáo cuối theo mục 5 của `worker-rules.md`.
