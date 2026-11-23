# T01 – Đặc tả chính thức và khung chữ ký Java (mốc M1)

**Worker:** codex, effort `high` · **Nhánh:** `feature/subject-program` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` (đặc biệt mục 3 và 9), `docs/de-xuat-de-tai-1.md` mục 4.

Đặc tả này là **căn cứ duy nhất** để hai người khác nhau cài `subject/` và `reference/` độc lập, và để viết test P1 mà không nhìn code. Mọi chỗ hai người đọc có thể hiểu khác nhau đều phải được viết rõ.

## Phạm vi

- `docs/spec/README.md`: mục lục, nguyên tắc chung (mục 3.1 của thiết kế), quy ước tiền và thời gian
- `docs/spec/api.md`: **chữ ký đóng băng**: mọi lớp, enum, ngoại lệ, hằng số, constructor, phương thức public/package-private, kiểu trả về
- `docs/spec/domain.md`: `Product`, `Coupon`, `CartItem`, `Order`, enum, ngoại lệ
- `docs/spec/cart.md`, `docs/spec/pricing.md`, `docs/spec/shipping.md`, `docs/spec/order.md`
- `docs/spec/branches.md`: bảng hàm → nhánh/tình huống cần kiểm thử (kèm các nhánh khó phủ của mục 3.7)
- `docs/spec/open-questions.md`
- `subject/src/main/java/shop/*.java`: **khung**, đủ chữ ký theo `api.md`; thân mọi phương thức và constructor chỉ có `throw new UnsupportedOperationException("chưa cài đặt");`. Xóa `subject/src/main/java/.gitkeep`.

**Không** tạo hay sửa `subject/pom.xml`: một worker khác đang làm. Để kiểm tra biên dịch, dùng `javac --release 17 -encoding UTF-8 -d <thư mục tạm ngoài repo hoặc .work/> <các file .java>`.

## Nội dung bắt buộc của từng file đặc tả hàm

Với mỗi phương thức: tiền điều kiện, **thứ tự kiểm tra** (đánh số), ngoại lệ và kiểu ngoại lệ, giá trị trả về, tác dụng phụ (trạng thái nào đổi, trạng thái nào **không** đổi khi ném ngoại lệ), giá trị biên, và **ít nhất 2 ví dụ số cụ thể** (đầu vào → đầu ra), có tính tay từng bước. Kiểm tra lại số học của mọi ví dụ.

## Quyết định đã chốt (orchestrator), phải ghi vào đặc tả

Lấy nguyên quy tắc mục 3.2–3.7 của thiết kế, cộng các điểm làm rõ sau:

1. Package `shop`. Mọi lớp là `public`. Các service có constructor không tham số; `OrderService` có thêm `OrderService(PricingService, ShippingCalculator)`, tham số `null` thì ném `IllegalArgumentException`. Constructor không tham số dùng `new PricingService()` và `new ShippingCalculator()`.
2. "Chuỗi rỗng" nghĩa là `null` hoặc `s.trim().isEmpty()`. Áp dụng cho `Product.id`, `Product.name`, `Coupon.code`, `orderId`.
3. `Product`: getter `getId()`, `getName()`, `getPrice()`, `getWeightGrams()`, `getStock()`. `restoreStock` không có giới hạn trên. Thứ tự kiểm tra constructor: `id`, `name`, `price`, `weightGrams`, `stock`.
4. `Coupon`: getter cho mọi trường. Với `FIXED`, `maxDiscount` được nhận (≥ 0) nhưng bị bỏ qua. Thứ tự kiểm tra constructor: `code`, `type`, `value`, (`PERCENT` thì `value <= 100`), `minOrder`, `maxDiscount`, `expiry`.
5. `CartItem` là đối tượng **bất biến**: `CartItem(Product product, int quantity)`, `getProduct()`, `getQuantity()`, `getLineTotal()` = `price × quantity` (kiểu `long`). Constructor: `product == null` hoặc `quantity <= 0` → `IllegalArgumentException`.
6. `Cart` giữ các dòng theo **thứ tự thêm lần đầu**, khóa theo `product.getId()`. `addItem` với sản phẩm đã có trong giỏ: kiểm tra tồn kho theo `p.getStock()` của đối tượng `p` vừa truyền vào, và dòng hàng lưu đối tượng `p` mới nhất. `getItems()` trả danh sách **không sửa được** (sửa ném `UnsupportedOperationException`). Hàm nào ném ngoại lệ thì giỏ **giữ nguyên**. `updateQuantity` và `removeItem` với `productId` rỗng: coi là không có trong giỏ (`NoSuchElementException`), sau khi đã qua bước kiểm tra `qty < 0` của `updateQuantity`.
7. `PricingService.calculateDiscount`: bước 1 kiểm tra `subtotal < 0` **hoặc** `today == null` → `IllegalArgumentException`; sau đó mới đến `coupon == null` → 0.
8. `ShippingCalculator.calculate`: phụ phí = `ceil((weightGrams − 1000) / 500) × 2000` khi `weightGrams > 1000`, ngược lại 0 (cài bằng vòng lặp, nhưng đặc tả ghi công thức). Ví dụ bắt buộc: 1000 → 0; 1001 → 2000; 1500 → 2000; 1501 → 4000; 20000 → 76000.
9. `Order`: constructor **package-private** (chỉ `OrderService` tạo). Getter: `getId()`, `getItems()` (danh sách không sửa được, bản sao tại thời điểm đặt), `getSubtotal()`, `getDiscount()`, `getShipping()`, `getTotal()`, `getStatus()`, `getCreatedAt()`, `getRegion()`. Đổi trạng thái qua phương thức package-private.
10. `placeOrder`: bước 1 kiểm tra `orderId` rỗng, `region == null`, **hoặc `now == null`** → `IllegalArgumentException`. **Không** thay đổi giỏ hàng (không xóa giỏ). `createdAt = now`. Nếu bất kỳ bước nào ném ngoại lệ thì **không** tồn kho nào bị trừ.
11. `cancelOrder`: `now == null` → `IllegalArgumentException` (cùng bước 1 với `order == null`). Mốc 24 giờ: `!now.isAfter(createdAt.plusHours(24))` thì hoàn `total`. Hủy đơn `PENDING` cũng hoàn tồn kho (vì tồn kho đã trừ lúc đặt).
12. Phép nhân tiền dùng `long`; đặc tả ghi rõ không xét tràn số (đầu vào hợp lệ nằm trong giới hạn: giá ≤ 10^9, số lượng ≤ 50).
13. Ngoại lệ riêng có constructor `(String message)`. Đặc tả **không** quy định nội dung message; test không được so sánh message.

Chỗ nào ngoài danh sách trên mà còn mơ hồ: chọn cách hợp lý nhất, ghi vào `docs/spec/open-questions.md` theo dạng `| # | Câu hỏi | Cách đã chọn | Trạng thái |`.

## Tiêu chí xong

- Lệnh `javac` trên khung exit 0 (ghi lệnh và output vào báo cáo).
- `api.md` liệt kê đủ mọi chữ ký có trong khung, và khung có đủ mọi chữ ký trong `api.md` (tự đối chiếu, nêu kết quả đối chiếu trong báo cáo).
- Mọi ví dụ số trong đặc tả đã được kiểm tra lại số học.
- Không có file nào ngoài phạm vi bị đổi (`git status --porcelain`).
