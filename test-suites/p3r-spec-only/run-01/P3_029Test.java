package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @CsvSource({
        "PENDING,0,0,0",
        "PENDING,48,0,0",
        "PAID,0,0,514999",
        "PAID,24,-1,514999",
        "PAID,24,0,514999",
        "PAID,24,1,399999",
        "PAID,24,1000000000,399999"
    })
    void cancellationRefundRespectsNanosecondDeadlineAndDiscountedGoods(
            OrderStatus initial, long hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 17, 345678901);
        Product p = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (initial == OrderStatus.PAID) {
            service.pay(order);
        }
        assertEquals(0, p.getStock());
        p.restoreStock(3);
        LocalDateTime cancelAt = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelAt));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelAt));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
