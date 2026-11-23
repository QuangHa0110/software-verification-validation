package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void orderServicePlaceOrderPropagatesPricingAndShippingExceptionsAtomically() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Sản phẩm", 10000L, 100, 5);
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Coupon expiredCoupon = new Coupon("C", CouponType.FIXED, 1000L, 0L, 0L, LocalDate.of(2026, 11, 20));
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D1", cart, expiredCoupon, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());

        Coupon notApplicable = new Coupon("C2", CouponType.FIXED, 1000L, 500000L, 0L, LocalDate.of(2026, 11, 25));
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D1", cart, notApplicable, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());

        Cart heavyCart = new Cart();
        Product pH1 = new Product("H1", "Nặng 1", 10L, 10000, 2);
        Product pH2 = new Product("H2", "Nặng 2", 20L, 10001, 2);
        heavyCart.addItem(pH1, 1);
        heavyCart.addItem(pH2, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D2", heavyCart, null, Region.INNER_CITY, now));
        assertEquals(2, pH1.getStock());
        assertEquals(2, pH2.getStock());
    }
}
