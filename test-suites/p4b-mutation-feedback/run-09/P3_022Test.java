package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

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
    void cancellationUsesExactTimeBoundaryAndRestoresOriginalProductsOnce(
            boolean paid, long hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 456789123);
        Product a = new Product("A", "Original", 200_000, 200, 4);
        Product b = new Product("B", "Second", 100_000, 600, 1);
        Product replacement = new Product("A", "Replacement", 9, 5, 10);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        assertEquals(2, a.getStock());
        assertEquals(0, b.getStock());
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(3);
        b.restoreStock(7);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertEquals(paid ? OrderStatus.PAID : OrderStatus.PENDING, order.getStatus());
        assertEquals(5, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());
        assertEquals(514_999L, order.getTotal());

        assertEquals(expectedRefund, service.cancelOrder(order, t.plusHours(hours).plusNanos(nanos)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, a.getStock());
        assertEquals(8, b.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, a.getStock());
        assertEquals(8, b.getStock());
        assertEquals(10, replacement.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        List<CartItem> lines = order.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
    }
}
