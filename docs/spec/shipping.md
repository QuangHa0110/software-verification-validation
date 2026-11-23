# Vận chuyển

## `ShippingCalculator()`

1. Không cần đầu vào hoặc kiểm tra. Tạo bộ tính phí không giữ trạng thái nghiệp vụ. Không có ngoại lệ nghiệp vụ.

Ví dụ 1: tạo 1 bộ tính, giao nội thành 1 g với tiền hàng 0 → phí 15000 + 0 = 15000. Ví dụ 2: tạo 2 bộ tính, giao ngoại thành 1 g với tiền hàng 500000 → cả hai tính 0 VND.

## `calculate(region, weightGrams, orderValue)`

1. `region == null`, `weightGrams <= 0` hoặc `orderValue < 0` → `IllegalArgumentException`. `orderValue` là tiền hàng **sau giảm giá**, chưa cộng phí vận chuyển.
2. `weightGrams > 20000` → `IllegalArgumentException`, kể cả khi giá trị đơn đạt ngưỡng miễn phí.
3. Nếu `orderValue >= 500000` và vùng là `INNER_CITY` hoặc `SUBURB`, trả 0 cho toàn bộ phí.
4. Lấy phí cơ bản theo `switch` vùng.

   | INNER_CITY | SUBURB | PROVINCE | REMOTE |
   |---:|---:|---:|---:|
   | 15000 | 25000 | 35000 | 55000 |

5. Đến 1000 g, phụ phí bằng 0. Trên 1000 g, cộng 2000 VND cho mỗi khối 500 g bắt đầu của phần vượt. Công thức là `ceil((weightGrams − 1000) / 500) × 2000`, trong đó tỉ số trong `ceil` là phân số chính xác. Khi cài bằng vòng lặp, mỗi lượt tính một khối cho đến khi hết phần vượt.
6. Trả `long` bằng phí cơ bản + phụ phí. Hàm chỉ đọc đầu vào; thành công hay lỗi đều không để lại trạng thái.

Với `INNER_CITY` và tiền hàng 499999 VND, mọi hàng hợp lệ đều phải trả phí.

| Khối lượng (g) | Phần vượt dương (g) | Số khối bắt đầu | Phụ phí (VND) | Phí trả về (VND) |
|---:|---:|---:|---:|---:|
| 1 | 0 | 0 | 0 | 15000 + 0 = 15000 |
| 1000 | 0 | 0 | 0 | 15000 + 0 = 15000 |
| 1001 | 1 | ceil(1/500) = 1 | 1 × 2000 = 2000 | 15000 + 2000 = 17000 |
| 1500 | 500 | 500/500 = 1 | 1 × 2000 = 2000 | 15000 + 2000 = 17000 |
| 1501 | 501 | ceil(501/500) = 2 | 2 × 2000 = 4000 | 15000 + 4000 = 19000 |
| 20000 | 19000 | 19000/500 = 38 | 38 × 2000 = 76000 | 15000 + 76000 = 91000 |
| 20001 | — | — | — | Bước 2 ném `IllegalArgumentException` |

Cùng 1501 g và 500000 VND: `SUBURB` → đúng ngưỡng → 0; `PROVINCE` → 35000 + 4000 = 39000; `REMOTE` → 55000 + 4000 = 59000.
