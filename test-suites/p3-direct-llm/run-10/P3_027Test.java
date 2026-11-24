package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0",
        "false,25,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationUsesExactTimeBoundaryRestoresStockOnceAndKeepsAmounts(
            boolean paid, int hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 876543210);
        Product p = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("ONE", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        p.restoreStock(3);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertEquals(paid ? OrderStatus.PAID : OrderStatus.PENDING, order.getStatus());
        assertEquals(3, p.getStock());
        assertEquals(expectedRefund, service.cancelOrder(order, t.plusHours(hours).plusNanos(nanos)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(2)));
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
