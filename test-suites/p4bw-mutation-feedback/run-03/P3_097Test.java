package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_097Test {

    @Test
    void orderServiceCancelPaidOrderWithin24HoursFullRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Bút", 250000L, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("C1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 23));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);
        service.pay(order);
        assertEquals(0, p.getStock());

        LocalDateTime cancelTime = now.plusHours(24);
        long refund = service.cancelOrder(order, cancelTime);
        assertEquals(514999L, refund);
        assertEquals(2, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
