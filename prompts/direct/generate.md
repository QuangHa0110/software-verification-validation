# Nhiệm vụ sinh bộ test JUnit 5 trực tiếp (P3 – Direct LLM)

Dựa trên hợp đồng API đóng băng, toàn bộ đặc tả nghiệp vụ và toàn bộ mã nguồn Java dưới đây, hãy viết bộ unit test JUnit 5 đầy đủ cho package `shop` nhằm tối đa hóa độ phủ câu lệnh và nhánh.

## Quy tắc oracle bổ sung (chốt tại M1)

Để bảo đảm mọi test hợp lệ đều khớp với đặc tả và bản cài đặt tham chiếu độc lập:

- Kiểm tra chuỗi rỗng dùng `s == null || s.trim().isEmpty()`, nhưng chuỗi hợp lệ được **lưu nguyên trạng** (không `trim()` khi lưu); so khớp ID phân biệt hoa thường bằng `String.equals`.
- `Cart.getItems()` và `Order.getItems()` trả về bản chụp danh sách không sửa được (mọi thao tác sửa trực tiếp hoặc qua iterator đều ném `UnsupportedOperationException`). Không kiểm tra đồng nhất tham chiếu (`assertSame`) giữa các đối tượng `List` hoặc `CartItem` từ các lần đọc khác nhau; chỉ kiểm tra nội dung qua getter và kiểm tra cùng tham chiếu `Product` khi đặc tả yêu cầu.
- Constructor package-private `Order(...)` kiểm tra đầy đủ tính hợp lệ của tham số và nhất quán số tiền, luôn khởi tạo trạng thái `PENDING`; `Order.setStatus(status)` chỉ từ chối `null` và gán enum, việc kiểm tra chuyển trạng thái hợp lệ thuộc về `OrderService`.
- Không có sổ kiểm tra trùng ID đơn hàng; mỗi lần `placeOrder` hợp lệ tạo một đơn mới nếu còn đủ tồn; giỏ không giữ chỗ tồn kho; `Coupon` bất biến và dùng lại được nhiều lần.
- `OrderService(pricingService, shippingCalculator)` giữ tham chiếu hai dịch vụ được tiêm và khi đặt đơn đi tới bước tính tiền sẽ gọi `calculateDiscount` trước rồi mới gọi `calculate`, truyền nguyên ngoại lệ nếu có mà không trừ tồn kho.
- Thời điểm `LocalDateTime` giữ nguyên độ chính xác đầu vào, không đổi múi giờ; ngày áp mã là `now.toLocalDate()`.
- Test chỉ kiểm tra kiểu ngoại lệ, không so sánh chuỗi message.

---

## 1. Hợp đồng API đóng băng (`docs/spec/api.md`)

{{API}}

---

## 2. Đặc tả nghiệp vụ đầy đủ (nguồn chuẩn cho oracle)

{{SPEC}}

---

## 3. Mã nguồn đối tượng (`subject/src/main/java/shop/*.java`)

{{SOURCE}}

---

## Yêu cầu đầu ra

- Chỉ trả về các khối mã ```` ```java ````, mỗi khối chứa **đúng một** phương thức `@Test` (hoặc `@ParameterizedTest` với provider nội tuyến) tự chứa hoàn toàn, không có khai báo `package`, không có lớp bao ngoài, không có hàm phụ, không có trường lớp, không gọi `now()`.
- Không sử dụng công cụ, không chạy lệnh, không đọc file.
- Toàn bộ kết quả kỳ vọng và ngoại lệ kỳ vọng phải tuân thủ tuyệt đối đặc tả ở Mục 1 và Mục 2.
