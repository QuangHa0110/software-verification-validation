package shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static shop.CartTest.assertPair;
import static shop.DomainTest.expectError;

class OrderTest {
    static final LocalDateTime PLACED = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123_456_789);
    private final Product a = new Product("A", "A", 30, 1, 2);
    private final Product b = new Product("B", "B", 40, 2, 3);
    private final List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 2), new CartItem(b, 1)));

    private Order makeOrder() {
        return new Order(" D ", source, 100, 7, 11, 104, PLACED, Region.REMOTE);
    }

    static void assertHeader(Order order, String id, LocalDateTime created, Region region,
                             OrderStatus status, long subtotal, long discount, long fee, long total) {
        // Các trường đóng băng bằng dữ liệu đặt đơn; chỉ trạng thái theo bước hiện tại.
        assertAll(() -> assertEquals(id, order.getId()), () -> assertEquals(created, order.getCreatedAt()),
                () -> assertSame(region, order.getRegion()), () -> assertSame(status, order.getStatus()),
                () -> assertEquals(subtotal, order.getSubtotal()), () -> assertEquals(discount, order.getDiscount()),
                () -> assertEquals(fee, order.getShipping()), () -> assertEquals(total, order.getTotal()));
    }

    private void checkStock() {
        // Constructor và setter không trừ hay hoàn kho: A vẫn 2, B vẫn 3.
        assertAll(() -> assertEquals(2, a.getStock()), () -> assertEquals(3, b.getStock()));
    }

    private void checkOrder(Order order, OrderStatus status) {
        // 30 × 2 + 40 × 1 = 100; 100 - 7 + 11 = 104.
        assertHeader(order, " D ", PLACED, Region.REMOTE, status, 100, 7, 11, 104);
        assertPair(order.getItems(), a, 2, b, 1);
        checkStock();
    }

    private void reject(String id, List<CartItem> lines, long subtotal, long discount, long fee,
                        long total, LocalDateTime created, Region region) {
        List<CartItem> before = lines == null ? null : new ArrayList<>(lines);
        // Dữ liệu sai ở trường đang xét, constructor phải từ chối.
        expectError(IllegalArgumentException.class,
                () -> new Order(id, lines, subtotal, discount, fee, total, created, region));
        // Constructor lỗi phải giữ nguyên danh sách nguồn và tồn của cả hai sản phẩm.
        assertEquals(before, lines);
        checkStock();
    }

    @Test
    void constructorSnapshotsSourceAndExposesNineGetters() {
        Order order = makeOrder();
        source.clear();
        checkOrder(order, OrderStatus.PENDING);
        // Chỉ số 0 hợp lệ, nhưng không được xóa khỏi bản chụp của đơn.
        expectError(UnsupportedOperationException.class, () -> order.getItems().remove(0));
        checkOrder(order, OrderStatus.PENDING);
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL", "''", "'  '"}, nullValues = "NULL")
    void constructorRequiresId(String id) {
        reject(id, source, 100, 7, 11, 104, PLACED, Region.REMOTE);
    }

    @Test
    void constructorRejectsNullLines() {
        reject("D", null, 0, 0, 0, 0, PLACED, Region.REMOTE);
    }

    @Test
    void constructorRejectsEmptyLines() {
        reject("D", new ArrayList<>(), 0, 0, 0, 0, PLACED, Region.REMOTE);
    }

    @Test
    void constructorRejectsNullElementAfterValidLine() {
        reject("D", Arrays.asList(source.get(0), null), 60, 0, 0, 60, PLACED, Region.REMOTE);
    }

    @ParameterizedTest
    @CsvSource({"99,7,11,103", "100,-1,11,112", "100,101,11,10",
                "100,7,-1,92", "100,7,11,103"})
    void constructorValidatesMoney(long subtotal, long discount, long shipping, long total) {
        // Tổng dòng = 30 × 2 + 40 = 100; giảm phải thuộc [0,100], phí >= 0, tổng = 100 - 7 + 11 = 104.
        reject("D", source, subtotal, discount, shipping, total, PLACED, Region.REMOTE);
    }

    @Test
    void constructorRequiresTime() {
        reject("D", source, 100, 7, 11, 104, null, Region.REMOTE);
    }

    @Test
    void constructorRequiresRegion() {
        reject("D", source, 100, 7, 11, 104, PLACED, null);
    }

    @Test
    void constructorAllowsFullDiscountAndZeroTotal() {
        Order order = new Order("D", source, 100, 100, 0, 0, PLACED, Region.SUBURB);
        // 100 - 100 + 0 = 0, cả dấu bằng của cận giảm và phí 0 đều hợp lệ.
        assertHeader(order, "D", PLACED, Region.SUBURB, OrderStatus.PENDING, 100, 100, 0, 0);
        assertPair(order.getItems(), a, 2, b, 1);
        checkStock();
    }

    @Test
    void setterRejectsNull() {
        Order order = makeOrder();
        // null không phải trạng thái; tất cả dữ liệu kể cả trạng thái ban đầu còn nguyên.
        expectError(IllegalArgumentException.class, () -> order.setStatus(null));
        checkOrder(order, OrderStatus.PENDING);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "PAID", "SHIPPED", "CANCELLED"})
    void setterOnlyChangesStatus(String name) {
        Order order = makeOrder();
        OrderStatus status = OrderStatus.valueOf(name);
        order.setStatus(status);
        // Setter gán trực tiếp từng hằng kể cả SHIPPED từ PENDING và không tác động kho.
        checkOrder(order, status);
    }
}
