package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0", "false,25,0,0", "true,0,0,514999",
        "true,24,-1,514999", "true,24,0,514999",
        "true,24,1,399999", "true,24,1000000000,399999"
    })
    void cancellationRefundUsesExactTwentyFourHourBoundaryAndDiscountedGoods(
            boolean paid, int hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 12, 31, 23, 59, 59, 123_456_789);
        Product p = new Product("P", "Name", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        assertEquals(0, p.getStock());
        p.restoreStock(3);
        LocalDateTime when = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, when));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, when));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(t, order.getCreatedAt());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
