# Giỏ hàng

Mỗi ID xuất hiện tối đa một lần. Thứ tự là thứ tự thêm lần đầu trong các dòng **hiện còn**. Cộng dồn hoặc đổi số lượng giữ vị trí; xóa rồi thêm lại đặt ở cuối. Mọi thao tác giỏ giữ nguyên tồn kho. Khi ném lỗi, cả thứ tự, số lượng và tham chiếu sản phẩm ở từng dòng đều giữ nguyên. Các ví dụ dùng số lượng tính theo đơn vị sản phẩm.

## `Cart()`

1. Không cần đầu vào hay kiểm tra; tạo giỏ riêng với 0 dòng. Không có ngoại lệ nghiệp vụ.

Ví dụ 1: tạo giỏ thứ nhất → 0 dòng → tổng 0 VND. Ví dụ 2: giỏ thứ nhất đã có 2 dòng → tạo giỏ thứ hai → số dòng hai giỏ lần lượt là 2 và 0.

## `addItem(p, qty)`

1. Nếu `p == null` hoặc `qty <= 0`, ném `IllegalArgumentException`.
2. Lấy số lượng của ID `p.getId()` (vắng mặt lấy 0), cộng `qty` theo giá trị toán học để được `newQty`.
3. Nếu `newQty > 50`, ném `IllegalArgumentException`.
4. Nếu `newQty > p.getStock()`, ném `InsufficientStockException`. Dùng tồn của đối tượng vừa truyền vào dù cùng ID với sản phẩm cũ.
5. Ghi dòng gồm chính `p` và `newQty`, kể cả khi giá hoặc khối lượng của `p` khác sản phẩm cũ. Kết thúc `void`.

- Đã có A(giá 9, tồn 1) × 1 rồi B × 1. Thêm đối tượng A mới (giá 12, tồn 50) × 49 → 1 + 49 = 50 → vừa đủ tồn → dòng đầu dùng A mới, thành tiền 12 × 50 = 600; B vẫn thứ hai.
- Đã có A × 49, thêm A mới tồn 3 với lượng 2 → 49 + 2 = 51 > 50 → `IllegalArgumentException` trước kiểm tra tồn; dòng A cũ vẫn 49.
- Giỏ rỗng, thêm A tồn 2 với lượng 3 → 0 + 3 = 3 ≤ 50 nhưng 3 > 2 → `InsufficientStockException`; vẫn 0 dòng.

## `updateQuantity(productId, qty)`

1. `qty < 0` → `IllegalArgumentException`.
2. ID rỗng hoặc không khớp dòng nào → `NoSuchElementException`.
3. `qty == 0` → xóa dòng và kết thúc `void`.
4. `qty > 50` → `IllegalArgumentException`.
5. `qty` lớn hơn tồn hiện tại của sản phẩm đang lưu → `InsufficientStockException`.
6. Thay số lượng bằng đúng `qty`, giữ sản phẩm và vị trí, kết thúc `void`. Kể cả khi số lượng yêu cầu bằng số cũ, vẫn kiểm tra tồn.

- A × 2, tồn 50 → cập nhật 50 → 50 ≤ 50 và vừa đủ tồn → A × 50. Cập nhật tiếp 0 → 1 − 1 = 0 dòng.
- Giỏ rỗng, ID rỗng và lượng −1 → −1 < 0 → `IllegalArgumentException`; cùng ID với lượng 51 → đã qua bước 1, thiếu dòng → `NoSuchElementException`. Cả hai lần giỏ có 0 dòng.
- A × 2, tồn 3 → yêu cầu 4 → 4 ≤ 50, 4 > 3 → `InsufficientStockException`, vẫn A × 2.

## `removeItem(productId)`

1. ID rỗng hoặc vắng → `NoSuchElementException`.
2. Xóa toàn bộ dòng khớp ID, các dòng còn lại giữ thứ tự. Kết thúc `void`.

Ví dụ 1: [A × 2, B × 3] → xóa A → 2 − 1 = 1 dòng [B × 3]. Ví dụ 2: [A × 2] → xóa B → 0 dòng khớp → `NoSuchElementException`, vẫn 1 dòng [A × 2].

## Các phép đọc

Mỗi hàng là hợp đồng của một phương thức. Tiền điều kiện duy nhất là giỏ đã được tạo. Mỗi hàm chỉ thực hiện bước 1 ghi trong bảng, không ném lỗi nghiệp vụ và không thay đổi giỏ hay sản phẩm. Hai cột cuối là hai phép tính độc lập cho **từng** hàm. Giỏ thứ hai theo thứ tự A (giá 12000, nặng 200 g) × 2 rồi B (giá 7000, nặng 150 g) × 3.

| Hàm | 1. Kết quả | Giỏ có 0 dòng | Giỏ có 2 dòng |
|---|---|---|---|
| `getSubtotal()` | Cộng `price × quantity` của mỗi dòng bằng `long` | Tổng rỗng → 0 VND | 12000 × 2 + 7000 × 3 = 24000 + 21000 = 45000 VND |
| `getTotalWeightGrams()` | Cộng `weightGrams × quantity`, trả `int` | Tổng rỗng → 0 g | 200 × 2 + 150 × 3 = 400 + 450 = 850 g |
| `isEmpty()` | Trả `true` khi số dòng bằng 0 | 0 == 0 → true | 2 == 0 → false |
| `getItems()` | Trả bản chụp danh sách không sửa được theo thứ tự thêm | 0 dòng → [] | Vị trí 0 → A × 2, vị trí 1 → B × 3. Xóa A khỏi giỏ thì bản cũ vẫn có 2 dòng |

Ví dụ tiền vượt `int`: 1 dòng giá 1000000000 × 3 → 3000000000 VND cho cả dòng lẫn tạm tính.
