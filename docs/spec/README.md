# Đặc tả giỏ hàng và thanh toán · M1

Bộ tài liệu này là hợp đồng chung của `subject/`, `reference/` và bộ test P1 trên Java 17. Người viết P1 lấy giá trị kỳ vọng từ các quy tắc và ví dụ dưới đây. Khung Java ở M1 chỉ phục vụ biên dịch.

| Khi cần tra | Tài liệu |
|---|---|
| Tên kiểu, phạm vi truy cập, chữ ký, hằng số | [API đóng băng](api.md) |
| Sản phẩm, mã giảm giá, dòng hàng, đơn hàng, enum và ngoại lệ | [Kiểu dữ liệu](domain.md) |
| Thứ tự dòng, giới hạn số lượng, tổng tiền và khối lượng | [Giỏ hàng](cart.md) |
| Điều kiện áp dụng mã và số tiền giảm | [Giảm giá](pricing.md) |
| Vùng giao, ngưỡng miễn phí và phụ phí khối lượng | [Vận chuyển](shipping.md) |
| Đặt, trả tiền, giao, hủy và hoàn tiền | [Vòng đời đơn hàng](order.md) |
| Tình huống và kết quả cần phân biệt trong P1 | [Bảng nhánh](branches.md) |
| Quyết định bổ sung để hai bản cài đặt dùng cùng cách hiểu | [Sổ câu hỏi](open-questions.md) |

## Quy ước áp dụng cho mọi hàm

1. Package là `shop`. Dữ liệu ở trong bộ nhớ. Mỗi ví dụ bắt đầu từ trạng thái được mô tả, độc lập với ví dụ khác. Không dùng database, mạng, đồng hồ hệ thống hay trạng thái nghiệp vụ toàn cục. Xét các lời gọi tuần tự.
2. Mỗi số tiền có kiểu `long`, đơn vị VND. Phép nhân liên quan đến tiền thực hiện bằng `long` ngay từ thừa số đầu. Dùng số nguyên, phép chia tiền giữ phần nguyên xuống. Khối lượng tính bằng gam. Chỉ phép đếm số khối 500 g bắt đầu mới làm tròn lên.
3. Không xét tràn số. Bộ dữ liệu nghiệp vụ dùng giá ≤ 10⁹ và số lượng hợp lệ trong giỏ ≤ 50; tổng, tích trung gian, tồn kho và khối lượng thuộc miền biểu diễn của các kiểu tương ứng. Đây là miền tính toán cho oracle. Chỉ những bước kiểm tra ghi ở từng hàm mới phát sinh lỗi nghiệp vụ. Các ca số lượng sai như 51 vẫn dùng để kiểm tra giới hạn giỏ.
4. Ngày dùng `LocalDate`; thời điểm dùng `LocalDateTime`, giữ nguyên đến nano giây, cùng hệ giờ địa phương của dữ liệu đầu vào. Không đổi múi giờ. Ngày xét mã khi đặt là `now.toLocalDate()`. Mốc 24 giờ tính từ lúc tạo đơn, trong miền biểu diễn của `plusHours(24)`.
5. “Rỗng” đối với chuỗi nghĩa là `s == null || s.trim().isEmpty()`. Giữ nguyên chuỗi hợp lệ, kể cả khoảng trắng đầu/cuối. So khớp ID bằng `String.equals`, phân biệt hoa thường. ID đơn do người gọi cấp.
6. Thực hiện các bước đánh số từ trên xuống, dừng tại bước trả kết quả hoặc ném lỗi đầu tiên. Trong một bước gồm các điều kiện “hoặc” cùng loại lỗi, chỉ cần một điều kiện đúng. Ngoại lệ đều unchecked. Các tên chuẩn là `java.lang.IllegalArgumentException`, `java.lang.IllegalStateException`, `java.util.NoSuchElementException`, `java.lang.UnsupportedOperationException`; ba ngoại lệ nghiệp vụ thuộc `shop`. Test kiểm tra kiểu ngoại lệ, không so sánh message.
7. Tiền điều kiện để *thành công* là các kiểm tra tương ứng đều đạt, trừ các nhánh trả sớm được nêu rõ. Constructor tạo một đối tượng mới khi thành công; khi lỗi, không có đối tượng trả về. Hàm `void` chỉ có kết quả quan sát được qua trạng thái. Ngoài các thay đổi nêu ở từng hàm, mọi đối tượng đầu vào giữ nguyên. Getter không nhận tham số, đọc từ một đối tượng đã tạo thành công, không thay đổi trạng thái và không ném ngoại lệ nghiệp vụ.
8. “Bản chụp dòng hàng” cố định thứ tự, tham chiếu sản phẩm và số lượng tại thời điểm chụp. Dòng bất biến có thể được dùng chung. Tồn kho thuộc đối tượng `Product` dùng chung; sửa giỏ sau đó không viết lại dòng đã chụp. Mọi thao tác sửa danh sách trả về (kể cả qua iterator) đều ném `UnsupportedOperationException`.
