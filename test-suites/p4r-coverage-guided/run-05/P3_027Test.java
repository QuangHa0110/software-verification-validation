package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void standardPricingFailuresPrecedeOverweightShippingAndAllThreeFailuresAreAtomic() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0);
        Coupon expired = new Coupon("X", CouponType.FIXED, 1, 31, 0, t.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 31, 0, t.toLocalDate());
        Product a = new Product("A", "A", 10, 10_000, 1);
        Product b = new Product("B", "B", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Runnable check = () -> {
            assertEquals(1, a.getStock());
            assertEquals(2, b.getStock());
            List<CartItem> rows = cart.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(1, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(1, rows.get(1).getQuantity());
            assertEquals(30L, cart.getSubtotal());
            assertEquals(20_001, cart.getTotalWeightGrams());
        };
        assertThrows(CouponExpiredException.class,
                () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
        check.run();
        assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D", cart, minimum, Region.REMOTE, t));
        check.run();
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
        check.run();
    }
}
