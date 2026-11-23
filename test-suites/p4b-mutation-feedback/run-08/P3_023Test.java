package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0",
        "false,48,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationRefundUsesExactElapsedTimeAndRestoresOnlyOriginalOrderLines(
            boolean paid, long hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123_456_789);
        Product a = new Product("A", "Original", 150_000, 200, 2);
        Product b = new Product("B", "Second", 200_000, 600, 1);
        Product replacement = new Product("A", "Replacement", 10, 3, 7);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, coupon, Region.INNER_CITY, t);
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
        if (paid) {
            service.pay(order);
        }
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(3);
        b.restoreStock(4);
        LocalDateTime cancelledAt = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelledAt));

        Runnable verify = () -> {
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(5, a.getStock());
            assertEquals(5, b.getStock());
            assertEquals(7, replacement.getStock());
            assertEquals(1, cart.getItems().size());
            assertSame(replacement, cart.getItems().get(0).getProduct());
            assertEquals(3, cart.getItems().get(0).getQuantity());
            assertEquals("O", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.INNER_CITY, order.getRegion());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelledAt));
        verify.run();
    }
}
