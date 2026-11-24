package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_049Test {

    @Test
    void orderServiceCancelPaidAfter24HoursPartialRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Item", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("FIXED1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 24));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);
        service.pay(order);

        p.restoreStock(3);
        long refund = service.cancelOrder(order, now.plusHours(24).plusSeconds(1));
        assertEquals(399999L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getStock());
    }
}
