package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void orderServicePlaceOrderPricingAndShippingErrorsPreserveStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Item", 100L, 10, 5);
        cart.addItem(p, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        Coupon expired = new Coupon("EXP", CouponType.FIXED, 10L, 0L, 0L, LocalDate.of(2026, 11, 22));
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D1", cart, expired, Region.INNER_CITY, time));
        assertEquals(5, p.getStock());

        Coupon tooHighMin = new Coupon("HIGH", CouponType.FIXED, 10L, 500L, 0L, LocalDate.of(2026, 11, 24));
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D2", cart, tooHighMin, Region.INNER_CITY, time));
        assertEquals(5, p.getStock());

        Cart heavyCart = new Cart();
        Product heavy1 = new Product("H1", "H1", 10L, 10000, 2);
        Product heavy2 = new Product("H2", "H2", 20L, 10001, 2);
        heavyCart.addItem(heavy1, 1);
        heavyCart.addItem(heavy2, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D3", heavyCart, null, Region.REMOTE, time));
        assertEquals(2, heavy1.getStock());
        assertEquals(2, heavy2.getStock());
    }
}
