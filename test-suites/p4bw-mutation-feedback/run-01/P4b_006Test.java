package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_006Test {

    @Test
    void killOrderServiceCancelOrderPaidAfter24HoursDiscountSubtraction() {
        OrderService orderService = new OrderService();
        Cart cart = new Cart();
        Product product = new Product("P1", "Product 1", 100_000L, 500, 10);
        cart.addItem(product, 1);
        Coupon coupon = new Coupon("FIXED20K", CouponType.FIXED, 20_000L, 0L, 0L, LocalDate.of(2026, 11, 25));
        LocalDateTime createdTime = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = orderService.placeOrder("ORD-1", cart, coupon, Region.INNER_CITY, createdTime);
        orderService.pay(order);

        // Cancel after 24 hours: refund = (subtotal - discount) * 80 / 100 = (100000 - 20000) * 80 / 100 = 64000
        // Mutant replaces with addition: (100000 + 20000) * 80 / 100 = 96000
        LocalDateTime cancelTime = createdTime.plusHours(24).plusSeconds(1);
        long refund = orderService.cancelOrder(order, cancelTime);
        assertEquals(64_000L, refund);
    }
}
