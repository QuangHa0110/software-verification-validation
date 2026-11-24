package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0", "false,25,0,0",
        "true,0,0,514999", "true,24,-1,514999", "true,24,0,514999",
        "true,24,1,399999", "true,24,1000000000,399999"
    })
    void refundUsesPaymentStateAndInclusiveNanosecondDeadline(
            boolean paid, int hours, long nanos, long expected) {
        Product p = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123_456_789);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        p.restoreStock(3);
        LocalDateTime cancellation = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expected, service.cancelOrder(order, cancellation));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(t, order.getCreatedAt());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancellation));
        assertEquals(5, p.getStock());
    }
}
