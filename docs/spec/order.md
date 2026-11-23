# Vòng đời đơn hàng

Đặt đơn trừ kho ngay. Trả tiền và giao hàng chỉ đổi trạng thái. Hủy đơn hợp lệ hoàn kho đúng một lần. Các ví dụ độc lập dưới đây dùng `t = 2026-11-23T08:00:00` và gọi các dịch vụ chuẩn trừ khi nêu khác.

## Hai constructor của OrderService

### `OrderService()`

1. Không có đầu vào hoặc điều kiện từ chối. Tạo dịch vụ với `new PricingService()` và `new ShippingCalculator()`, tương đương truyền hai đối tượng này cho constructor bên dưới. Không có ngoại lệ nghiệp vụ và không đụng tới giỏ, đơn hay kho.

Ví dụ 1: tạo 1 dịch vụ → đặt 1 món giá 1, nặng 1 g, đủ tồn, nội thành, không mã → giảm 0, phí 15000, tổng 1 + 15000 = 15001. Ví dụ 2: tạo dịch vụ thứ hai → 1 món giá 500000 với các điều kiện còn lại như trên → đúng ngưỡng miễn phí → tổng 500000 + 0 = 500000.

### `OrderService(pricingService, shippingCalculator)`

1. Một trong hai tham số null → `IllegalArgumentException`.
2. Giữ chính hai đối tượng được cấp và dùng chúng cho các lần đặt đơn sau; constructor không gọi phép tính nào. Không tạo hay sửa giỏ, đơn hoặc kho.

Ví dụ 1: truyền 1 bộ tính giá và 0 bộ tính phí (null) → bước 1 ném lỗi, 0 đơn được tạo. Ví dụ 2: tiêm hai bộ tính thử trả lần lượt 7 và 11 VND → khởi tạo thành công, 0 lần tính trong constructor. Khi đặt 1 món giá 100, nặng 1 g và đủ tồn → gọi tính giảm với 100 → còn 100 − 7 = 93; gọi tính phí với 93 → tổng 93 + 11 = 104.

## `placeOrder(orderId, cart, coupon, region, now)`

1. `orderId` rỗng, `region == null` hoặc `now == null` → `IllegalArgumentException`.
2. `cart == null` hoặc giỏ rỗng → `IllegalStateException`. Mã null là đầu vào hợp lệ.
3. Theo thứ tự dòng trong giỏ, kiểm tra lượng từng dòng với tồn **hiện tại** của chính sản phẩm ở dòng đó. Nếu lượng lớn hơn tồn, ném `InsufficientStockException` ngay; lượng bằng tồn là đủ.
4. Lấy `subtotal = cart.getSubtotal()`. Gọi đúng một lần `pricingService.calculateDiscount(subtotal, coupon, now.toLocalDate())`; lấy `goods = subtotal − discount`. Gọi đúng một lần `shippingCalculator.calculate(region, cart.getTotalWeightGrams(), goods)`. Nếu một phép tính ném ngoại lệ, truyền nguyên ngoại lệ đó ra và dừng. Với dịch vụ chuẩn có thể gặp `CouponExpiredException`, `CouponNotApplicableException`, hoặc `IllegalArgumentException` do khối lượng.
5. Tính `total = goods + shipping`. Đơn giữ ID gốc, bản chụp dòng hàng, các khoản tiền vừa tính, `createdAt = now`, vùng đã truyền và trạng thái PENDING.
6. Chỉ ghi nhận việc đặt khi mọi kiểm tra và phép tính thành công. Trừ đúng lượng từng dòng trên sản phẩm của dòng đó, trả đơn mới. Nếu bất kỳ bước nào ném ngoại lệ, không trả đơn và **không sản phẩm nào bị trừ tồn**, kể cả những dòng đã kiểm tra trước dòng lỗi.

Cả khi thành công và khi lỗi, giỏ giữ nguyên nội dung, số lượng và thứ tự. Trên đường thành công, tham chiếu sản phẩm dùng chung phản ánh tồn mới. Mỗi lần gọi hợp lệ tạo một đơn riêng, kể cả khi dùng lại ID và giỏ còn đủ tồn.

| Đầu vào | Tính tay và kết quả quan sát |
|---|---|
| "D1", A giá 250000 × 2, nặng 500 g/đơn vị, tồn 2; FIXED 1 còn hiệu lực, tối thiểu 0, INNER_CITY, t | Đủ tồn → tạm tính 250000 × 2 = 500000 → giảm 1 → hàng 499999 → nặng 500 × 2 = 1000 g, phí 15000 → tổng 514999. D1 PENDING tạo tại t; tồn 2 − 2 = 0, giỏ vẫn A × 2 |
| "D2", cùng giỏ ban đầu nhưng không mã, SUBURB, t | 500000 − 0 = 500000 → miễn toàn bộ phí → tổng 500000; tồn 2 − 2 = 0. Sửa giỏ sau khi đặt không đổi số lượng 2 trong đơn |
| "D3", A giá 10, nặng 10000 g, tồn 1 và B giá 20, nặng 10001 g, tồn 2; mỗi dòng × 1, không mã, REMOTE, t | Hai dòng đều đủ → tạm tính 10 + 20 = 30 → hàng 30 → 10000 + 10001 = 20001 g → `IllegalArgumentException`; tồn A vẫn 1, B vẫn 2, giỏ vẫn có 2 dòng |
| "D4", A × 1 tồn 1, B × 2 đã giảm tồn từ 2 còn 1 sau khi thêm; mã hết hạn, INNER_CITY, t | A đủ, B thiếu vì 2 > 1 → `InsufficientStockException` trước khi xét mã; cả A và B vẫn có tồn 1 |

## `pay(order)`

1. `order == null` → `IllegalArgumentException`.
2. Trạng thái khác PENDING → `IllegalStateException`.
3. Đổi PENDING thành PAID, kết thúc `void`. Giữ nguyên các khoản tiền, thời điểm, dòng hàng và tồn kho. Khi lỗi, trạng thái cũng giữ nguyên.

Ví dụ 1: D1 PENDING, tổng 514999, tồn 0 → trả tiền lần 1 → PAID, tổng 514999, tồn 0. Ví dụ 2: D1 đã PAID → lần 2 ném `IllegalStateException`, vẫn PAID với tổng 514999.

## `ship(order)`

1. `order == null` → `IllegalArgumentException`.
2. Trạng thái khác PAID → `IllegalStateException`.
3. Đổi PAID thành SHIPPED, kết thúc `void`. Chỉ trường trạng thái đổi. Khi lỗi, cả đơn và tồn giữ nguyên.

Ví dụ 1: D2 tổng 500000, đã trả tiền 1 lần → giao lần 1 → SHIPPED, tổng 500000, tồn 0. Ví dụ 2: D2 trả tiền 0 lần, trạng thái PENDING → giao lần 1 → `IllegalStateException`, vẫn PENDING, tồn 0.

## `cancelOrder(order, now)`

1. `order == null` hoặc `now == null` → `IllegalArgumentException`.
2. SHIPPED hoặc CANCELLED → `IllegalStateException` trước kiểm tra ngày giờ.
3. `now.isBefore(createdAt)` → `IllegalArgumentException`; bằng thời điểm tạo là hợp lệ.
4. PENDING → tiền hoàn bằng 0 dù đã đặt bao lâu. PAID và `!now.isAfter(createdAt.plusHours(24))` → hoàn đúng `total` gồm phí vận chuyển. PAID muộn hơn mốc đó → hoàn `(subtotal − discount) × 80 / 100` bằng `long`, lấy phần nguyên, không cộng phí.
5. Cộng lại số lượng trong **bản chụp của đơn** vào tồn hiện tại của từng đối tượng sản phẩm tương ứng, kể cả khi hủy PENDING. Không lấy tồn ban đầu gán đè và không dùng giỏ hiện tại.
6. Đổi trạng thái thành CANCELLED; trả số VND hoàn kiểu `long`. Dòng hàng và tiền ghi trên đơn vẫn giữ nguyên; không sửa giỏ. Khi ném lỗi, trạng thái đơn, các khoản tiền và mọi tồn kho giữ nguyên. Hủy lần hai không hoàn lặp.

Dùng các bản độc lập của D1 ở trên; mỗi bản có tiền hàng sau giảm 499999, phí 15000 và lượng đặt 2.

| Trạng thái và thời điểm | Tính tay và kết quả |
|---|---|
| PENDING, đúng t, tồn hiện tại 0 | Hoàn 0; tồn 0 + 2 = 2 → CANCELLED |
| PAID, 2026-11-24T08:00:00 | t + 24 giờ vừa đúng → hoàn 499999 + 15000 = 514999; tồn 0 + 2 = 2 → CANCELLED |
| PAID, 2026-11-24T08:00:01, tồn hiện tại 3 do vừa nhập thêm hàng | 24 giờ + 1 giây → 499999 × 80 = 39999920 → chia 100 lấy 399999 VND; tồn 3 + 2 = 5 → CANCELLED |
| PAID, 2026-11-23T07:59:59 | Sớm 1 giây → `IllegalArgumentException`; vẫn PAID, tồn 0 |
| SHIPPED, 2026-11-23T07:59:59 | Đã giao → `IllegalStateException` ở bước 2, dù sớm 1 giây; tồn 0 |
