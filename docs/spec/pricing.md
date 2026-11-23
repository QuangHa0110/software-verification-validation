# Giảm giá

## `PricingService()`

1. Không có tham số hoặc điều kiện kiểm tra. Tạo một dịch vụ tính giảm giá không giữ trạng thái nghiệp vụ. Không có ngoại lệ nghiệp vụ.

Ví dụ 1: tạo 1 dịch vụ, dùng với tạm tính 0, không mã và ngày hợp lệ → giảm 0 VND. Ví dụ 2: tạo 2 dịch vụ rồi dùng mỗi dịch vụ với tạm tính 200, mã 10% hợp lệ không trần → mỗi dịch vụ trả 200 × 10 / 100 = 20 VND.

## `calculateDiscount(subtotal, coupon, today)`

1. Nếu `subtotal < 0` hoặc `today == null`, ném `IllegalArgumentException`.
2. Nếu `coupon == null`, trả 0.
3. Nếu `today.isAfter(coupon.getExpiry())`, ném `CouponExpiredException`. Ngày hết hạn vẫn dùng được.
4. Nếu `subtotal < coupon.getMinOrder()`, ném `CouponNotApplicableException`. Bằng ngưỡng thì đi tiếp.
5. Với `PERCENT`, tính `d = subtotal × value / 100` theo phép chia nguyên. Nếu `maxDiscount > 0` và `d > maxDiscount`, lấy `d = maxDiscount`.
6. Với `FIXED`, lấy `d = min(value, subtotal)`. Mọi giá trị hợp lệ của `maxDiscount` đều cho cùng kết quả.
7. Trả `d` VND, kiểu `long`, trong đoạn từ 0 đến `subtotal`. Phép tính thuần, kể cả khi lỗi; mã dùng được nhiều lần.

Trong bảng dưới, ngày hết hạn là 2026-11-23; ngày tính mặc định cũng là 2026-11-23.

| Đầu vào | Tính tay theo thứ tự |
|---|---|
| 99999, PERCENT 15, tối thiểu 99999, trần 10000 | Đúng ngày, đúng ngưỡng → 99999 × 15 = 1499985 → chia 100 lấy 14999 → vượt trần → 10000 |
| 99999, cùng mã với trần 0 | 1499985 / 100 lấy 14999 → trần 0 không giới hạn → 14999 |
| 100000, PERCENT 10, tối thiểu 0, trần 10000 | 100000 × 10 / 100 = 10000 → bằng trần → 10000 |
| 7, PERCENT 1, tối thiểu 0, trần 0 | 7 × 1 / 100 lấy 0 → 0 |
| 300, FIXED 400, tối thiểu 0, trần 1 | min(400, 300) = 300 → giảm 300, tiền hàng còn 0 |
| 0, FIXED 400, tối thiểu 0, trần 1 | min(400, 0) = 0 → giảm 0 |
| 99998, mã ở hàng đầu, tính ngày 2026-11-24 | Muộn 1 ngày → `CouponExpiredException` trước khi xét thiếu 1 VND |
| 99998, mã ở hàng đầu, tính đúng ngày hết hạn | Ngày đạt, 99998 < 99999 → `CouponNotApplicableException` |
| 0, mã null, ngày null | Ngày sai ở bước 1 → `IllegalArgumentException`; thay bằng ngày hợp lệ → bước 2 trả 0 |
