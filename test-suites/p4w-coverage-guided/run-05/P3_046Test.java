package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void testOrderServiceCancelPaidAfter24HoursPartialRefund() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 250000L, 500, 5);
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F1", CouponType.FIXED, 1L, 0L, 0L, now.toLocalDate());
        Order order = service.placeOrder("ORD-1", cart, coupon, Region.INNER_CITY, now);
        service.pay(order);

        long expectedRefund = (order.getSubtotal() - order.getDiscount()) * 80 / 100;
        long refund = service.cancelOrder(order, now.plusHours(24).plusSeconds(1));
        assertEquals(expectedRefund, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
    }
}
