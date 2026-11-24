package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_048Test {

    @Test
    void orderServicePlaceOrderPropagatesPricingAndShippingExceptions() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product pA = new Product("A", "Prod A", 1000L, 10, 5);
        cart.addItem(pA, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Coupon expiredCoupon = new Coupon("EXP", CouponType.PERCENT, 10L, 0L, 0L, LocalDate.of(2026, 11, 22));
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D1", cart, expiredCoupon, Region.INNER_CITY, now));
        assertEquals(5, pA.getStock());

        Coupon highMinCoupon = new Coupon("MIN", CouponType.PERCENT, 10L, 50000L, 0L, LocalDate.of(2026, 11, 23));
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D1", cart, highMinCoupon, Region.INNER_CITY, now));
        assertEquals(5, pA.getStock());

        Cart heavyCart = new Cart();
        Product heavy1 = new Product("H1", "Heavy 1", 10L, 10000, 1);
        Product heavy2 = new Product("H2", "Heavy 2", 20L, 10001, 2);
        heavyCart.addItem(heavy1, 1);
        heavyCart.addItem(heavy2, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D2", heavyCart, null, Region.REMOTE, now));
        assertEquals(1, heavy1.getStock());
        assertEquals(2, heavy2.getStock());
    }
}
