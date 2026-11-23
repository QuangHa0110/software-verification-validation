# Kiểu dữ liệu

Kiểu và phạm vi truy cập của từng thành viên nằm trong [API](api.md). Các ví dụ A/B trong mỗi bảng là hai bộ đầu vào độc lập. Với **mỗi getter** trong bảng, thứ tự chỉ có một bước: **1.** đọc và trả đúng giá trị ở hàng tương ứng. Getter dùng đối tượng đã tạo, không có điều kiện từ chối, không ghi dữ liệu. Mọi constructor dưới đây giữ nguyên các đối tượng được truyền vào, kể cả khi lỗi.

## Enum

Thứ tự khai báo cũng là thứ tự của mảng `values()`, bắt đầu từ chỉ số 0. Enum không có dữ liệu nghiệp vụ khả biến.

| Kiểu | Giá trị theo chỉ số | Ý nghĩa |
|---|---|---|
| `Region` | 0 INNER_CITY, 1 SUBURB, 2 PROVINCE, 3 REMOTE | Nội thành, ngoại thành, tỉnh, vùng xa |
| `CouponType` | 0 PERCENT, 1 FIXED | Phần trăm, số tiền cố định |
| `OrderStatus` | 0 PENDING, 1 PAID, 2 SHIPPED, 3 CANCELLED | Chờ trả tiền, đã trả, đã giao cho vận chuyển, đã hủy |

- `values()` của từng enum: **1.** không nhận đầu vào, trả mảng mới chứa toàn bộ hằng theo thứ tự trên. Không ném lỗi nghiệp vụ. Hai ví dụ cho mỗi kiểu là đọc phần tử đầu/cuối: Region có 4 phần tử → [0] INNER_CITY, [4 − 1] REMOTE; CouponType có 2 → [0] PERCENT, [2 − 1] FIXED; OrderStatus có 4 → [0] PENDING, [4 − 1] CANCELLED. Sửa một phần tử mảng không ảnh hưởng lần gọi sau.
- `valueOf(name)` của từng enum: **1.** null → `NullPointerException` theo Java; **2.** tên khác chính xác mọi tên khai báo (kể cả khác hoa/thường hoặc có khoảng trắng thừa) → `IllegalArgumentException`; **3.** trả hằng cùng tên, không thay đổi trạng thái. Hai ví dụ cho mỗi kiểu: Region nhận "SUBURB" → vị trí 1 → SUBURB, "REMOTE" → 3 → REMOTE; CouponType nhận "PERCENT" → 0 → PERCENT, "FIXED" → 1 → FIXED; OrderStatus nhận "PAID" → 1 → PAID, "SHIPPED" → 2 → SHIPPED.

## Ngoại lệ riêng

`InsufficientStockException(String message)`, `CouponExpiredException(String message)`, `CouponNotApplicableException(String message)` đều kế thừa trực tiếp `RuntimeException`. Mỗi constructor thực hiện **1.** nhận bất kỳ chuỗi nào, kể cả rỗng hoặc null, và tạo một ngoại lệ đúng kiểu. Không tự ném lỗi và không có tác dụng phụ nghiệp vụ. Thông điệp không thuộc oracle; test không so sánh message. Hai ví dụ cho từng constructor:

| Constructor | Đầu vào thứ nhất → kết quả | Đầu vào thứ hai → kết quả |
|---|---|---|
| InsufficientStockException | "0" → tạo 1 InsufficientStockException | null → tạo 1 InsufficientStockException |
| CouponExpiredException | "1" → tạo 1 CouponExpiredException | null → tạo 1 CouponExpiredException |
| CouponNotApplicableException | "2" → tạo 1 CouponNotApplicableException | null → tạo 1 CouponNotApplicableException |

## Product

### `Product(id, name, price, weightGrams, stock)`

Mỗi bước 1–5 ném `IllegalArgumentException` khi điều kiện sai. Kiểm tra theo thứ tự:

1. `id` phải khác rỗng.
2. `name` phải khác rỗng.
3. `price > 0`.
4. `weightGrams > 0`.
5. `stock >= 0`.
6. Lưu nguyên năm giá trị. Chỉ `stock` có thể thay đổi sau khi tạo.

Ví dụ A: ("P1", "Bút 1", 1, 1, 0) → hai chuỗi đạt → 1 > 0, 1 > 0, 0 ≥ 0 → sản phẩm A. Ví dụ B: (" P2 ", "Vở 2", 30000, 500, 7) → ID sau trim đủ để qua kiểm tra → 30000 > 0, 500 > 0, 7 ≥ 0 → sản phẩm B, giữ hai khoảng trắng của ID. Thay giá của B bằng 0 và tồn bằng −1 → bước 3 ném lỗi.

| Getter | 1. Giá trị | A → kết quả | B → kết quả |
|---|---|---|---|
| `getId()` | ID gốc | trường 1 → "P1" | trường 1 → `" P2 "` |
| `getName()` | Tên gốc | trường 2 → "Bút 1" | trường 2 → "Vở 2" |
| `getPrice()` | Đơn giá VND | trường 3 → 1 | trường 3 → 30000 |
| `getWeightGrams()` | Gam mỗi đơn vị | trường 4 → 1 | trường 4 → 500 |
| `getStock()` | Tồn tại lúc gọi | ban đầu → 0 | ban đầu 7, sau reduceStock(2) → 7 − 2 = 5 |

### `reduceStock(q)`

1. `q <= 0` → `IllegalArgumentException`.
2. `q > stock` → `InsufficientStockException`.
3. Trừ đúng `q` khỏi tồn hiện tại, kết thúc `void`. Lỗi giữ nguyên cả năm trường.

Ví dụ 1: tồn 7, q = 7 → 7 ≤ 7 → tồn 7 − 7 = 0. Ví dụ 2: tồn 1, q = 2 → 2 > 1 → `InsufficientStockException`, tồn vẫn 1. Với tồn 0 và q = 0, bước 1 ném `IllegalArgumentException`.

### `restoreStock(q)`

1. `q <= 0` → `IllegalArgumentException`, mọi trường giữ nguyên.
2. Cộng đúng `q` vào tồn hiện tại, kết thúc `void`. Không áp trần nghiệp vụ, kể cả mức tồn ban đầu hay giới hạn 50 của giỏ.

Ví dụ 1: tồn 50, q = 2 → 2 > 0 → tồn 50 + 2 = 52. Ví dụ 2: tồn 0, q = 0 → 0 ≤ 0 → lỗi, tồn vẫn 0.

## Coupon

### `Coupon(code, type, value, minOrder, maxDiscount, expiry)`

Dừng và ném `IllegalArgumentException` ở bước đầu tiên không đạt:

1. `code` khác rỗng.
2. `type != null`.
3. `value > 0`.
4. Nếu `type == PERCENT`, cần `value <= 100`. FIXED chấp nhận giá trị lớn hơn 100.
5. `minOrder >= 0`.
6. `maxDiscount >= 0` cho cả hai loại.
7. `expiry != null`. Constructor không xét ngày hiện tại.
8. Lưu nguyên sáu trường bất biến. Trần 0 là không giới hạn đối với PERCENT; FIXED luôn bỏ qua trần khi tính giảm giá.

Ví dụ A: ("C1", PERCENT, 100, 0, 0, 2026-11-23) → 0 < 100 ≤ 100, hai ngưỡng 0 đạt, ngày có giá trị → mã A. Ví dụ B: ("C2", FIXED, 150, 30, 1, 2026-11-24) → 150 > 0, bỏ kiểm tra 100, 30 ≥ 0, 1 ≥ 0 → mã B. Nếu B có trần −1 → lỗi ở bước 6 dù là FIXED.

| Getter | 1. Trường trả về | A → kết quả | B → kết quả |
|---|---|---|---|
| `getCode()` | code gốc | trường 1 → "C1" | trường 1 → "C2" |
| `getType()` | type | trường 2 → PERCENT | trường 2 → FIXED |
| `getValue()` | value theo đơn vị của loại | trường 3 → 100 (%) | trường 3 → 150 (VND) |
| `getMinOrder()` | minOrder (VND) | trường 4 → 0 | trường 4 → 30 |
| `getMaxDiscount()` | maxDiscount đã nhận (VND) | trường 5 → 0 | trường 5 → 1 |
| `getExpiry()` | ngày cuối còn hiệu lực | trường 6 → 2026-11-23 | trường 6 → 2026-11-24 |

## CartItem

### `CartItem(product, quantity)`

1. `product == null` → `IllegalArgumentException`.
2. `quantity <= 0` → `IllegalArgumentException`.
3. Tạo dòng bất biến giữ chính sản phẩm và số lượng truyền vào. Constructor không kiểm tra tồn hay giới hạn 50; giỏ thực hiện hai kiểm tra này. Tạo dòng không giữ chỗ hay trừ tồn kho.

Ví dụ A: sản phẩm P1 giá 1 tồn 0, lượng 1 → có sản phẩm, 1 > 0 → dòng A, tồn vẫn 0. Ví dụ B: sản phẩm P2 giá 30000 tồn 7, lượng 2 → có sản phẩm, 2 > 0 → dòng B, tồn vẫn 7. Lượng 0 bị từ chối ở bước 2.

| Getter | 1. Trả về | A → kết quả | B → kết quả |
|---|---|---|---|
| `getProduct()` | Cùng tham chiếu sản phẩm | dòng 1 đơn vị → chính P1 | dòng 2 đơn vị → chính P2 |
| `getQuantity()` | Lượng cố định | đầu vào 1 → 1 | đầu vào 2 → 2 |
| `getLineTotal()` | Đơn giá × lượng, kiểu long | 1 × 1 = 1 VND | 30000 × 2 = 60000 VND |

## Order

### `Order(id, items, subtotal, discount, shipping, total, createdAt, region)`

Constructor package-private dành cho `OrderService`. Nó nhận kết quả đã tính và luôn khởi tạo trạng thái PENDING. Quy tắc kiểm tra dữ liệu nội bộ dưới đây áp dụng cả khi gọi trực tiếp trong package. Mỗi điều kiện sai ở bước 1–6 ném `IllegalArgumentException`.

1. ID đơn khác rỗng.
2. Danh sách dòng khác null, có ít nhất một dòng và mọi phần tử khác null.
3. `subtotal` bằng tổng các `getLineTotal()`.
4. `0 <= discount <= subtotal`.
5. `shipping >= 0` và `total == subtotal − discount + shipping`.
6. `createdAt` và `region` khác null.
7. Giữ bản chụp không sửa được của các dòng theo thứ tự đầu vào và lưu nguyên các giá trị còn lại. Constructor không trừ tồn, không xét lại mã hay biểu phí.

Hai bộ tham số mẫu, dùng luôn cho các getter:

- A: ID "D1", [P2 giá 30000 × 2], giảm 6000, phí 15000, tạo 2026-11-23T08:00 ở INNER_CITY → tạm tính 30000 × 2 = 60000, tổng 60000 − 6000 + 15000 = 69000. Truyền 60000 và 69000 tương ứng → tạo đơn A, PENDING.
- B: ID "D2", [P3 giá 500000 × 1], giảm 0, phí 0, tạo 2026-11-24T09:30 ở SUBURB → tạm tính 500000 × 1 = 500000, tổng 500000 − 0 + 0 = 500000. Truyền hai giá trị này → tạo đơn B, PENDING. Thay riêng total bằng 499999 → lỗi bước 5.

| Getter | 1. Trả về | A → kết quả | B → kết quả |
|---|---|---|---|
| `getId()` | ID gốc | đơn 1 → "D1" | đơn 2 → "D2" |
| `getItems()` | Danh sách chụp khi đặt, không sửa được | vị trí 0 → P2 × 2 | vị trí 0 → P3 × 1 |
| `getSubtotal()` | Tạm tính lúc đặt | 30000 × 2 = 60000 | 500000 × 1 = 500000 |
| `getDiscount()` | Số tiền giảm đã tính | 6000 đã lưu → 6000 | 0 đã lưu → 0 |
| `getShipping()` | Phí đã tính | 15000 đã lưu → 15000 | 0 đã lưu → 0 |
| `getTotal()` | Tổng lúc đặt | 60000 − 6000 + 15000 = 69000 | 500000 − 0 + 0 = 500000 |
| `getStatus()` | Trạng thái hiện tại | sau 0 lần pay → PENDING | sau 1 lần pay thành công → PAID |
| `getCreatedAt()` | Thời điểm đặt | đơn 1 → 2026-11-23T08:00 | đơn 2 → 2026-11-24T09:30 |
| `getRegion()` | Vùng giao lúc đặt | đơn 1 → INNER_CITY | đơn 2 → SUBURB |

### `setStatus(status)`

1. `status == null` → `IllegalArgumentException`; toàn bộ đơn và tồn kho giữ nguyên.
2. Gán giá trị enum vào trạng thái, kết thúc `void`. Setter package-private chỉ ghi trường trạng thái. `OrderService` chịu trách nhiệm kiểm tra chuyển tiếp hợp lệ và điều chỉnh tồn kho khi hủy.

Ví dụ 1: đơn D1 PENDING → setStatus(PAID) → trạng thái PAID, tổng vẫn 69000. Ví dụ 2: D2 PAID → setStatus(SHIPPED) → trạng thái SHIPPED, tổng vẫn 500000.
