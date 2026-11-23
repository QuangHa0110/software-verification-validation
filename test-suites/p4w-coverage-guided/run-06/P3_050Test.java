package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_050Test {

    @Test
    void orderServiceCancelOrderPaidAfter24HoursPartialRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Pen", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 23));
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = service.placeOrder("O1", cart, coupon, Region.INNER_CITY, created);
        service.pay(order);
        p.restoreStock(3);
        assertEquals(6, p.getStock());

        LocalDateTime after24h = created.plusHours(24).plusSeconds(1);
        long refund = service.cancelOrder(order, after24h);
        assertEquals(399999L, refund);
        assertEquals(8, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
