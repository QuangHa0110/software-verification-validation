# Bảng tình huống cho P1

Mỗi hàng nêu đầu vào cần phân biệt và kết quả quan sát qua API. Các ca trong cùng hàng độc lập, trừ chỗ ghi rõ là chuỗi thao tác. Với mọi ca lỗi của giỏ, so lại thứ tự, đối tượng sản phẩm và lượng của từng dòng; với ca lỗi đặt/hủy, so lại tồn của **tất cả** sản phẩm. Đối chiếu kiểu ngoại lệ, không so message.

## Kiểu dữ liệu và giỏ

| Hàm | Nhánh hoặc biên | Kết quả cần kiểm tra |
|---|---|---|
| Các enum: `values`, `valueOf` | Thứ tự khai báo; tên đúng, sai, null | Đủ hằng theo bảng domain; mảng riêng; đúng hằng, IllegalArgumentException, NullPointerException tương ứng |
| Ba constructor ngoại lệ | Message thường, null | Tạo được từng kiểu con của RuntimeException |
| Product(...) | Lần lượt làm sai id, name, price, weightGrams, stock; chuỗi null, rỗng, chỉ khoảng trắng; giá/khối lượng 0 và 1; tồn −1 và 0 | IllegalArgumentException tại kiểm tra đầu tiên sai; giá và khối lượng 1, tồn 0 hợp lệ |
| Product.getId/getName/getPrice/getWeightGrams/getStock | Chuỗi có khoảng trắng, tồn đổi sau khi giảm/phục hồi | Giữ nguyên chuỗi, giá, khối lượng; getter tồn phản ánh thay đổi |
| reduceStock / restoreStock | q âm, 0, 1; giảm vừa hết và thiếu 1; hoàn vượt tồn ban đầu và 50 | Sai dấu → IllegalArgumentException; thiếu → InsufficientStockException; giảm vừa hết → 0, hoàn cộng vào tồn hiện tại |
| Coupon(...) và 6 getter | Sai lần lượt code/type/value/minOrder/maxDiscount/expiry; PERCENT 0, 1, 100, 101; FIXED 101; trần FIXED âm, 0 và dương | IllegalArgumentException cho đầu vào sai; FIXED 101 hợp lệ, getter vẫn trả trần dương đã nhận; expiry có thể trong quá khứ |
| CartItem(...), getProduct/getQuantity/getLineTotal | null, lượng −1/0/1; tạo dòng độc lập với tồn; 3 đơn vị × 1000000000 | Lỗi tham số hoặc dòng bất biến trỏ đúng sản phẩm; thành tiền 3000000000 |
| Cart(), isEmpty, getSubtotal, getTotalWeightGrams | Giỏ rỗng, một dòng, nhiều dòng khác giá/cân nặng; xóa dòng cuối | true/0/0 khi rỗng; tính tổng từng dòng theo đúng đơn vị, không trừ tồn |
| addItem | p null, qty âm/0; ID mới; trùng ID cùng/khác đối tượng; newQty 50/51; vừa đủ/thiếu tồn | Cộng dồn, dùng tồn và thông tin của p mới; giữ vị trí đầu, thay tham chiếu chỉ khi thành công |
| addItem, lỗi chồng nhau | Đã có 49, thêm 2, p mới tồn 3 | 51 > 50 → IllegalArgumentException trước InsufficientStockException; dòng cũ nguyên vẹn |
| updateQuantity | ID thiếu/rỗng với −1/0/51; ID có với 0/1/50/51; lượng vượt tồn, bằng tồn; giữ nguyên lượng khi tồn đã giảm | −1 → IllegalArgumentException trước tra ID; thiếu với 0/51 → NoSuchElementException; có với 0 → xóa; có với 51 và thiếu tồn → IllegalArgumentException trước lỗi tồn |
| removeItem, getItems | ID null/trắng/lạ; xóa giữa; xóa rồi thêm lại; cập nhật sau khi lấy danh sách; thử sửa danh sách không rỗng | Vắng → NoSuchElementException; thứ tự còn lại ổn định, thêm lại ở cuối; bản chụp cũ nguyên vẹn, sửa ném UnsupportedOperationException |

## Giảm giá và vận chuyển

| Hàm | Nhánh hoặc biên | Kết quả cần kiểm tra |
|---|---|---|
| PricingService(), calculateDiscount | subtotal −1/0, today null với cả mã null; ngày hợp lệ, mã null | Sai subtotal hoặc today → IllegalArgumentException trước nhánh mã null; còn lại không mã → 0 |
| calculateDiscount, thời hạn và tối thiểu | Hôm trước, đúng ngày và hôm sau expiry; đơn dưới ngưỡng 1, bằng và trên ngưỡng; đồng thời hết hạn và dưới ngưỡng | Đúng ngày vẫn áp dụng; hết hạn → CouponExpiredException trước CouponNotApplicableException |
| calculateDiscount, PERCENT | Tại minOrder 99999, mã 15%, ngày đúng expiry; trần 0, 14999, 10000, 15000 | 99999 × 15 / 100 lấy 14999 → tiền giảm lần lượt 14999, 14999, 10000, 14999; phân biệt bằng, vượt và dưới trần |
| calculateDiscount, FIXED | value nhỏ hơn, bằng, lớn hơn subtotal; trần dương nhỏ hơn value | Trả số nhỏ hơn giữa value và subtotal, kể cả subtotal 0; bỏ qua trần |
| ShippingCalculator(), calculate | Từng lỗi region null, cân nặng −1/0, orderValue −1; cân nặng 20001 dù giá trị đạt 500000 | IllegalArgumentException trước xét miễn phí |
| calculate, ngưỡng tiền | Cả bốn vùng ở 499999 và 500000, 1000 g; trên ngưỡng với 20000 g | Tại 499999 trả đúng phí cơ bản từng vùng. Từ 500000 chỉ INNER_CITY/SUBURB trả 0, kể cả phụ phí |
| calculate, vòng lặp phụ phí | Khi phải trả phí, cân nặng lần lượt 1000, 1001, 1500, 1501, 20000, 20001 g | Phụ phí lần lượt 0, 2000, 2000, 4000, 76000 VND; cuối cùng IllegalArgumentException |

## Đơn hàng

| Hàm | Nhánh hoặc biên | Kết quả cần kiểm tra |
|---|---|---|
| Order(...) và 9 getter | Dữ liệu hợp lệ; mỗi điều kiện sai ở các bước 1–6 của constructor; sửa danh sách nguồn sau khi tạo; thử sửa danh sách đơn | Đúng các giá trị đóng băng tại lúc đặt, trạng thái đầu PENDING; lỗi dữ liệu → IllegalArgumentException; sửa danh sách trả về → UnsupportedOperationException |
| Order.setStatus | null và giá trị enum | null → IllegalArgumentException; giá trị hợp lệ chỉ đổi trạng thái |
| Hai constructor OrderService | Mặc định; từng phụ thuộc null; hai bộ tính được tiêm | Mặc định dùng dịch vụ chuẩn; null → IllegalArgumentException; đường thành công dùng đúng đối tượng được truyền |
| placeOrder, đầu vào | Từng lỗi ID rỗng, region null, now null với giỏ rỗng/null; đầu đơn hợp lệ với giỏ null/rỗng | IllegalArgumentException trước IllegalStateException; giỏ sai không gọi tính tiền |
| placeOrder, kiểm tra kho | Tồn vừa đủ, thiếu ở dòng sau và có mã hết hạn | Thiếu → InsufficientStockException trước tính giảm; dòng đầu không bị trừ |
| placeOrder, lỗi sau kiểm tra kho | Mã hết hạn, dưới ngưỡng, lỗi từ bộ tính được tiêm; nhiều dòng tổng 20001 g | Truyền đúng ngoại lệ, giữ toàn bộ tồn và giỏ, không trả đơn; lỗi giảm giá xảy ra trước tính phí |
| placeOrder, ngưỡng sau giảm giá | Nội thành, 1000 g, subtotal 500000: FIXED 1 và không mã | goods 499999 → phí 15000 → total 514999; không mã → goods 500000 → phí 0 → total 500000 |
| placeOrder, bản chụp và nguyên tử | Nhiều dòng, sửa giỏ hoặc thay sản phẩm cùng ID sau khi đặt; gọi lại cùng ID khi vẫn đủ tồn | Trừ từng sản phẩm đúng một lần mỗi đơn; giỏ còn nguyên sau đặt; đơn cũ giữ đúng dòng, tiền, thời điểm, vùng |
| pay | null; PENDING/PAID/SHIPPED/CANCELLED | null → IllegalArgumentException; chỉ PENDING đi tiếp thành PAID, còn lại IllegalStateException |
| ship | null; PENDING/PAID/SHIPPED/CANCELLED | null → IllegalArgumentException; chỉ PAID đi tiếp thành SHIPPED, còn lại IllegalStateException |
| cancelOrder, thứ tự lỗi | null order/now; now null trên đơn đã giao; SHIPPED/CANCELLED với now trước lúc tạo; PENDING/PAID với giờ sớm | null → IllegalArgumentException; trạng thái cấm → IllegalStateException trước lỗi thời điểm; các trạng thái còn lại với giờ sớm → IllegalArgumentException |
| cancelOrder, mốc thời gian | PENDING ngay khi tạo và sau 24 giờ; PAID lúc tạo, đúng 24 giờ, 24 giờ + 1 giây | PENDING hoàn 0 và hoàn kho; PAID đến đúng hạn hoàn cả phí; quá hạn chỉ hoàn 80% hàng sau giảm, lấy phần nguyên |
| cancelOrder, kho và gọi lặp | Nhiều dòng; đã sửa giỏ, nhập thêm kho hoặc thay sản phẩm cùng ID; hủy hai lần | Hoàn đúng lượng cho đối tượng gốc của từng dòng; lần 2 → IllegalStateException, không cộng tồn lần nữa |
