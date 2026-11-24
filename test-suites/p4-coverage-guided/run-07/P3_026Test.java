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
        "false,0,0,0",
        "false,48,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationUsesExactNanosecondDeadlineAndRefundsTheSpecifiedAmount(
            boolean paid, int hours, long nanos, long expectedRefund) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Product p = new Product("A", "First", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        if (paid) {
            service.pay(order);
        }
        assertEquals(0, p.getStock());
        p.restoreStock(3);
        LocalDateTime cancelledAt = time.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelledAt));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelledAt));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
