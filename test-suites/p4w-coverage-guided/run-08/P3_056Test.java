package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_056Test {

    @Test
    void orderServiceCancelPaidOrderAfter24Hours() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Item", 250000L, 500, 5);
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("FIXED1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 23));
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, created);
        service.pay(order);
        assertEquals(3, p.getStock());

        p.restoreStock(3);
        assertEquals(6, p.getStock());

        LocalDateTime cancelTime = created.plusHours(24).plusSeconds(1);
        long refund = service.cancelOrder(order, cancelTime);
        assertEquals(399999L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getStock());
    }
}
