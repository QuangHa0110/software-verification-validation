# Quyết định làm rõ tại M1

Các lựa chọn dưới đây bổ sung cho các quyết định đã chốt trong brief. Cả hai bản cài đặt và P1 áp dụng cùng cách hiểu.

| # | Câu hỏi | Cách đã chọn | Trạng thái |
|---|---|---|---|
| 1 | Chuỗi qua kiểm tra rỗng có được chuẩn hóa? | Chỉ dùng trim để kiểm tra; lưu chuỗi gốc, so ID bằng String.equals, phân biệt hoa thường. | Đã chọn tại M1 |
| 2 | Danh sách trả từ giỏ còn nhìn thấy thay đổi về sau? | Mỗi lần đọc cho một bản chụp không sửa được. Thứ tự, tham chiếu sản phẩm và số lượng của các dòng đã chụp giữ nguyên. | Đã chọn tại M1 |
| 3 | Xóa rồi thêm lại ID ở vị trí nào? | Cuối giỏ. Thay dòng đang có cùng ID thì giữ vị trí. Update kiểm tra tồn trên sản phẩm hiện lưu, kể cả khi giữ nguyên lượng dương. | Đã chọn tại M1 |
| 4 | Sao chép sâu đến đâu? | Giỏ và đơn có thể dùng chung CartItem bất biến. Mỗi dòng giữ chính Product được truyền vào, nên tồn hiện tại được dùng chung. Hủy hoàn vào đúng đối tượng lúc đặt. | Đã chọn tại M1 |
| 5 | Giới hạn giá, lượng và biểu diễn số có thêm điều kiện từ chối? | Dùng miền tính toán trong README, không thêm kiểm tra tràn. CartItem chỉ xét product và lượng dương; giỏ mới kiểm tra 50 và tồn. restoreStock không có trần nghiệp vụ. | Đã chọn tại M1 |
| 6 | Constructor nội bộ Order nhận gì và xử lý dữ liệu tự mâu thuẫn thế nào? | Nhận ID, dòng, bốn khoản tiền, thời điểm, vùng theo api.md; kiểm tra cấu trúc và nhất quán số tiền theo domain.md, khởi tạo PENDING. | Đã chọn tại M1 |
| 7 | Ai xác thực chuyển trạng thái? | Setter package-private setStatus từ chối null, chỉ gán enum; OrderService xác thực trạng thái nguồn và thực hiện tác dụng phụ. | Đã chọn tại M1 |
| 8 | Có kiểm tra trùng ID đơn, giữ chỗ trong giỏ hoặc dùng hết mã? | ID không có sổ đăng ký. Mỗi lần đặt là một đơn mới nếu còn đủ tồn. Giỏ không giữ chỗ; mã bất biến, tái sử dụng được. | Đã chọn tại M1 |
| 9 | So thời gian ở độ chính xác và múi giờ nào? | So LocalDateTime nguyên độ chính xác đầu vào; không đổi múi giờ, ngày xét mã lấy từ thời điểm đặt. Dữ liệu cho phép biểu diễn mốc cộng 24 giờ. | Đã chọn tại M1 |
| 10 | Các đối tượng dịch vụ được tiêm dùng như thế nào? | Giữ nguyên tham chiếu; trong mỗi lần đặt đi đến bước tính tiền, gọi tính giảm rồi tính phí tối đa một lần mỗi hàm. Truyền nguyên lỗi; hai bộ tính tuân thủ hợp đồng chỉ đọc. | Đã chọn tại M1 |
| 11 | Xét đồng thời hay chỉ chuỗi lời gọi xác định? | Hợp đồng và ví dụ xét lời gọi tuần tự. Tính nguyên tử bao trùm tất cả dòng trong một lần đặt. | Đã chọn tại M1 |
| 12 | Các kiểu miền có cho kế thừa và so các bản chụp thế nào? | Product, Coupon, CartItem, Order là final. Đối chiếu dữ liệu qua getter, kiểm tra tham chiếu Product khi cần. Không quy định đồng nhất tham chiếu của danh sách hay CartItem giữa hai lần đọc. | Đã chọn tại M1 |
