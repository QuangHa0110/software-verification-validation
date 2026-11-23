package shop;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static shop.CartTest.assertPair;
import static shop.DomainTest.DAY;
import static shop.DomainTest.expectError;
import static shop.OrderTest.PLACED;
import static shop.OrderTest.assertHeader;

class OrderLifecycleTest {
    private final OrderService service = new OrderService();
    private final Product a = new Product("A", "A", 200_000, 300, 3);
    private final Product b = new Product("B", "B", 100_000, 400, 5);
    private final Cart cart = new Cart();

    private Order place() {
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, DAY);
        return service.placeOrder("D", cart, coupon, Region.INNER_CITY, PLACED);
    }

    private Order reach(OrderStatus status) {
        Order order = place();
        switch (status) {
            case PAID -> service.pay(order);
            case SHIPPED -> {
                service.pay(order);
                service.ship(order);
            }
            case CANCELLED -> service.cancelOrder(order, PLACED);
            case PENDING -> { /* Trạng thái ngay sau đặt. */ }
        }
        return order;
    }

    private void checkFrozenOrder(Order order, OrderStatus status) {
        // 200000 × 2 + 100000 = 500000; 500000 - 1 + 15000 = 514999.
        assertHeader(order, "D", PLACED, Region.INNER_CITY, status, 500_000, 1, 15_000, 514_999);
        assertPair(order.getItems(), a, 2, b, 1);
    }

    private void checkState(Order order, OrderStatus status) {
        checkFrozenOrder(order, status);
        // A còn 3 - 2 = 1, B còn 5 - 1 = 4; nếu đã hủy thì cộng lại 2/1 thành 3/5.
        assertAll(() -> assertEquals(status == OrderStatus.CANCELLED ? 3 : 1, a.getStock()),
                () -> assertEquals(status == OrderStatus.CANCELLED ? 5 : 4, b.getStock()));
        // Trả tiền, giao, hủy đều giữ giỏ [A × 2, B × 1].
        assertPair(cart.getItems(), a, 2, b, 1);
    }

    @Test
    void payRequiresOrder() {
        // Thiếu order ở bước 1.
        expectError(IllegalArgumentException.class, () -> service.pay(null));
    }

    @Test
    void payMovesPendingToPaid() {
        Order order = place();
        service.pay(order);
        // Một lần trả tiền chỉ chuyển PENDING thành PAID.
        checkState(order, OrderStatus.PAID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PAID", "SHIPPED", "CANCELLED"})
    void payRejectsOtherStates(String name) {
        OrderStatus start = OrderStatus.valueOf(name);
        Order order = reach(start);
        // Mỗi trạng thái khác PENDING đều bị từ chối và giữ nguyên.
        expectError(IllegalStateException.class, () -> service.pay(order));
        checkState(order, start);
    }

    @Test
    void shipRequiresOrder() {
        // Thiếu order ở bước 1.
        expectError(IllegalArgumentException.class, () -> service.ship(null));
    }

    @Test
    void shipMovesPaidToShipped() {
        Order order = reach(OrderStatus.PAID);
        service.ship(order);
        // Một lần giao chỉ chuyển PAID thành SHIPPED.
        checkState(order, OrderStatus.SHIPPED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SHIPPED", "CANCELLED"})
    void shipRejectsOtherStates(String name) {
        OrderStatus start = OrderStatus.valueOf(name);
        Order order = reach(start);
        // Mỗi trạng thái khác PAID đều bị từ chối và giữ nguyên.
        expectError(IllegalStateException.class, () -> service.ship(order));
        checkState(order, start);
    }

    @Test
    void cancelRequiresOrder() {
        // Thiếu order với thời điểm hợp lệ.
        expectError(IllegalArgumentException.class, () -> service.cancelOrder(null, PLACED));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SHIPPED"})
    void cancelRequiresTimeBeforeCheckingState(String name) {
        OrderStatus start = OrderStatus.valueOf(name);
        Order order = reach(start);
        // Thiếu thời điểm ở bước 1, kể cả khi SHIPPED đồng thời vi phạm bước 2.
        expectError(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        checkState(order, start);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SHIPPED", "CANCELLED"})
    void cancelChecksForbiddenStateBeforeEarlyTime(String name) {
        OrderStatus start = OrderStatus.valueOf(name);
        Order order = reach(start);
        // Giờ hủy sớm 1 giây, nhưng hai trạng thái cấm phải dừng trước so thời điểm.
        expectError(IllegalStateException.class, () -> service.cancelOrder(order, PLACED.minusSeconds(1)));
        checkState(order, start);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "PAID"})
    void cancelRejectsTimeBeforeCreation(String name) {
        OrderStatus start = OrderStatus.valueOf(name);
        Order order = reach(start);
        // Trạng thái cho phép hủy nhưng thời điểm sớm hơn lúc tạo 1 nano giây.
        expectError(IllegalArgumentException.class, () -> service.cancelOrder(order, PLACED.minusNanos(1)));
        checkState(order, start);
    }

    @ParameterizedTest
    @CsvSource({"PENDING,0,0", "PENDING,86400,0", "PENDING,86401,0",
                "PAID,0,514999", "PAID,86400,514999", "PAID,86401,399999"})
    void refundAtCreationAndAround24Hours(OrderStatus start, long seconds, long expected) {
        Order order = reach(start);
        LocalDateTime cancelledAt = PLACED.plusSeconds(seconds);
        // PENDING hoàn 0. PAID đến 24 × 3600 = 86400 s hoàn 514999; thêm 1 s: 499999 × 80 / 100 → 399999.
        assertEquals(expected, service.cancelOrder(order, cancelledAt));
        checkState(order, OrderStatus.CANCELLED);
    }

    @Test
    void paidOneNanosecondPastDeadlineGetsGoodsOnly() {
        Order order = reach(OrderStatus.PAID);
        // Muộn 1 ns vẫn quá hạn: 499999 × 80 / 100 → 399999, bỏ phí 15000.
        assertEquals(399_999L, service.cancelOrder(order, PLACED.plusHours(24).plusNanos(1)));
        checkState(order, OrderStatus.CANCELLED);
    }

    @Test
    void lateRefundUsesLongArithmetic() {
        Product expensive = new Product("L", "L", 1_000_000_000L, 1, 3);
        cart.addItem(expensive, 3);
        Order order = service.placeOrder("L", cart, null, Region.REMOTE, PLACED);
        service.pay(order);
        // 3000000000 × 80 / 100 = 2400000000 VND, không hoàn phí REMOTE 55000.
        assertEquals(2_400_000_000L, service.cancelOrder(order, PLACED.plusHours(25)));
        // 3 - 3 + 3 = 3; tổng gốc 3000000000 + 55000 = 3000055000.
        assertAll(() -> assertEquals(3, expensive.getStock()), () -> assertEquals(OrderStatus.CANCELLED, order.getStatus()),
                () -> assertEquals(3_000_055_000L, order.getTotal()));
    }

    @Test
    void repeatedCancellationUsesOriginalProductsAndRestoresOnce() {
        Order order = reach(OrderStatus.PAID);
        a.restoreStock(7);
        b.restoreStock(2);
        Product replacement = new Product("A", "new", 1, 1, 4);
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        // Hoàn trước hạn: 499999 + 15000 = 514999.
        assertEquals(514_999L, service.cancelOrder(order, PLACED.plusHours(1)));
        checkAfterRestock(order, replacement);
        // Hủy lần hai ở giờ hợp lệ nhưng trạng thái đã là CANCELLED.
        expectError(IllegalStateException.class, () -> service.cancelOrder(order, PLACED.plusHours(2)));
        checkAfterRestock(order, replacement);
    }

    private void checkAfterRestock(Order order, Product replacement) {
        checkFrozenOrder(order, OrderStatus.CANCELLED);
        // A gốc: 3 - 2 + 7 + 2 = 10; B: 5 - 1 + 2 + 1 = 7. A mới chưa đặt nên còn 4.
        assertAll(() -> assertEquals(10, a.getStock()), () -> assertEquals(7, b.getStock()),
                () -> assertEquals(4, replacement.getStock()));
        // Giỏ sau khi sửa chỉ còn A mới × (2 + 1) = 3, kể cả sau lần hủy bị từ chối.
        assertEquals(1, cart.getItems().size());
        CartTest.assertLine(cart.getItems().get(0), replacement, 3);
    }
}
