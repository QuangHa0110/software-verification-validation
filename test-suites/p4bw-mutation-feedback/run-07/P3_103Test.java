package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_103Test {

    @Test
    void orderServiceCancelPaidOrderAfter24HoursPartialRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P01", "Book", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("FIXED1", CouponType.FIXED, 1L, 0L, 0L, now.toLocalDate());
        Order order = service.placeOrder("ORD1", cart, coupon, Region.INNER_CITY, now);

        service.pay(order);
        long refund = service.cancelOrder(order, now.plusHours(24).plusSeconds(1));

        long expectedRefund = (order.getSubtotal() - order.getDiscount()) * 80 / 100;
        assertEquals(expectedRefund, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
    }
}
