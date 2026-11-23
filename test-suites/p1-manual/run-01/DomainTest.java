package shop;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {
    static final LocalDate DAY = LocalDate.of(2026, 11, 23);

    static <E extends Throwable> E expectError(Class<E> expected, Executable operation) {
        // Mỗi nhánh lỗi trong đặc tả quy định một kiểu ngoại lệ cụ thể.
        E error = assertThrows(expected, operation);
        // Đối chiếu cả lớp thực tế vì assertThrows cũng nhận lớp con.
        assertEquals(expected, error.getClass());
        return error;
    }

    private static <E extends Enum<E>> void checkEnum(E[] expected, Supplier<E[]> values,
                                                       Function<String, E> valueOf) {
        E[] first = values.get();
        // Mảng đầu tiên chứa đủ hằng theo thứ tự khai báo trong domain.md.
        assertArrayEquals(expected, first);
        first[0] = null;
        // Sửa phần tử 0 của lần đọc trước không sửa mảng của lần đọc sau.
        assertArrayEquals(expected, values.get());
        for (E constant : expected) {
            // Tên đúng trả về chính hằng đã khai báo.
            assertSame(constant, valueOf.apply(constant.name()));
        }
        // Tên có khoảng trắng thừa không khớp hằng nào.
        expectError(IllegalArgumentException.class, () -> valueOf.apply(" " + expected[0].name()));
        // Tên null có kiểu lỗi riêng theo hợp đồng enum.
        expectError(NullPointerException.class, () -> valueOf.apply(null));
    }

    @Test
    void regionEnumeration() {
        checkEnum(new Region[]{Region.INNER_CITY, Region.SUBURB, Region.PROVINCE, Region.REMOTE},
                Region::values, Region::valueOf);
    }

    @Test
    void couponEnumeration() {
        checkEnum(new CouponType[]{CouponType.PERCENT, CouponType.FIXED},
                CouponType::values, CouponType::valueOf);
    }

    @Test
    void statusEnumeration() {
        checkEnum(new OrderStatus[]{OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED,
                OrderStatus.CANCELLED}, OrderStatus::values, OrderStatus::valueOf);
    }

    @ParameterizedTest
    @CsvSource(value = {"message", "''", "NULL"}, nullValues = "NULL")
    void domainExceptionsAcceptMessages(String message) {
        // Cả ba constructor nhận chuỗi thường, rỗng hoặc null và tạo con trực tiếp của RuntimeException.
        assertAll(
                () -> assertEquals(RuntimeException.class, new InsufficientStockException(message).getClass().getSuperclass()),
                () -> assertEquals(RuntimeException.class, new CouponExpiredException(message).getClass().getSuperclass()),
                () -> assertEquals(RuntimeException.class, new CouponNotApplicableException(message).getClass().getSuperclass()));
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL,N,1,1,0", "'',N,1,1,0", "'  ',N,1,1,0",
                        "A,NULL,1,1,0", "A,'',1,1,0", "A,'  ',1,1,0",
                        "A,N,0,1,0", "A,N,1,0,0", "A,N,1,1,-1"}, nullValues = "NULL")
    void invalidProduct(String id, String name, long price, int grams, int stock) {
        // Mỗi bộ vi phạm đúng một trường bắt buộc hoặc một cận dưới.
        expectError(IllegalArgumentException.class, () -> new Product(id, name, price, grams, stock));
    }

    @Test
    void productKeepsFieldsAtLowerBounds() {
        Product p = new Product(" A ", " Name ", 1, 1, 0);
        // Giá 1, nặng 1 g và tồn 0 đều hợp lệ. Hai chuỗi giữ nguyên khoảng trắng.
        assertAll(() -> assertEquals(" A ", p.getId()), () -> assertEquals(" Name ", p.getName()),
                () -> assertEquals(1L, p.getPrice()), () -> assertEquals(1, p.getWeightGrams()),
                () -> assertEquals(0, p.getStock()));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void stockRejectsNonpositiveChanges(int quantity) {
        Product p = new Product("A", "A", 1, 1, 0);
        // q <= 0 dừng trước khi kiểm tra thiếu tồn.
        expectError(IllegalArgumentException.class, () -> p.reduceStock(quantity));
        // Không có lần giảm hợp lệ, tồn vẫn bằng 0.
        assertEquals(0, p.getStock());
        // q <= 0 cũng không phải lượng hoàn hợp lệ.
        expectError(IllegalArgumentException.class, () -> p.restoreStock(quantity));
        // Không có lần hoàn hợp lệ, tồn vẫn bằng 0.
        assertEquals(0, p.getStock());
    }

    @Test
    void stockCanReachZeroAndExceedOriginalStock() {
        Product p = new Product("A", "A", 7, 13, 1);
        // Yêu cầu 2 khi còn 1, thiếu đúng 1.
        expectError(InsufficientStockException.class, () -> p.reduceStock(2));
        // Lần giảm lỗi giữ tồn ban đầu.
        assertEquals(1, p.getStock());
        p.reduceStock(1);
        // 1 - 1 = 0.
        assertEquals(0, p.getStock());
        p.restoreStock(1);
        // 0 + 1 = 1.
        assertEquals(1, p.getStock());
        p.restoreStock(50);
        // 1 + 50 = 51; chỉ tồn được sửa.
        assertAll(() -> assertEquals(51, p.getStock()), () -> assertEquals("A", p.getId()),
                () -> assertEquals("A", p.getName()), () -> assertEquals(7L, p.getPrice()),
                () -> assertEquals(13, p.getWeightGrams()));
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL,PERCENT,1,0,0", "'',PERCENT,1,0,0", "'  ',PERCENT,1,0,0",
                        "C,NULL,1,0,0", "C,PERCENT,0,0,0", "C,PERCENT,101,0,0", "C,FIXED,0,0,0",
                        "C,FIXED,101,-1,0", "C,FIXED,101,0,-1", "C,PERCENT,1,0,-1"}, nullValues = "NULL")
    void invalidCoupon(String code, CouponType type, long value, long minimum, long cap) {
        // Mỗi bộ chỉ sai một điều kiện của mã; trần âm sai với cả hai loại.
        expectError(IllegalArgumentException.class, () -> new Coupon(code, type, value, minimum, cap, DAY));
    }

    @Test
    void couponRequiresExpiry() {
        // Các trường tiền đều hợp lệ, thiếu ngày ở bước cuối.
        expectError(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 1, 0, 0, null));
    }

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,0,0", "FIXED,101,30,0", "FIXED,101,30,2"})
    void couponKeepsAllSixFields(CouponType type, long value, long minimum, long cap) {
        LocalDate expiry = LocalDate.of(2001, 1, 1);
        Coupon c = new Coupon(" C ", type, value, minimum, cap, expiry);
        // PERCENT nhận 1..100; FIXED nhận 101. Lưu cả trần FIXED và ngày trong quá khứ.
        assertAll(() -> assertEquals(" C ", c.getCode()), () -> assertSame(type, c.getType()),
                () -> assertEquals(value, c.getValue()), () -> assertEquals(minimum, c.getMinOrder()),
                () -> assertEquals(cap, c.getMaxDiscount()), () -> assertEquals(expiry, c.getExpiry()));
    }

    @Test
    void lineRequiresProduct() {
        // Lượng 1 hợp lệ nhưng thiếu sản phẩm.
        expectError(IllegalArgumentException.class, () -> new CartItem(null, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void lineRequiresPositiveQuantity(int quantity) {
        Product p = new Product("A", "A", 1, 1, 1);
        // Lượng -1/0 không tạo được dòng.
        expectError(IllegalArgumentException.class, () -> new CartItem(p, quantity));
        // Constructor lỗi không giữ chỗ, tồn vẫn 1.
        assertEquals(1, p.getStock());
    }

    @ParameterizedTest
    @CsvSource({"1,1000000000", "3,3000000000", "51,51000000000"})
    void lineTotalUsesLongAndIgnoresStock(int quantity, long total) {
        Product p = new Product("A", "A", 1_000_000_000L, 2, 0);
        CartItem line = new CartItem(p, quantity);
        // 1/3/51 × 1 tỉ = 1/3/51 tỉ VND. Tạo dòng độc lập với tồn và giới hạn giỏ.
        assertAll(() -> assertSame(p, line.getProduct()), () -> assertEquals(quantity, line.getQuantity()),
                () -> assertEquals(total, line.getLineTotal()), () -> assertEquals(0, p.getStock()));
    }
}
