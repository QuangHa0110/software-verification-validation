package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void standardPlacementChecksExpiryThenMinimumThenWeightWithoutPartialChanges() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Product a = new Product("A", "First", 10, 10_000, 1);
        Product b = new Product("B", "Second", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 31, 0, t.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 31, 0, t.toLocalDate());

        Runnable verify = () -> {
            assertEquals(1, a.getStock());
            assertEquals(2, b.getStock());
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
            assertEquals(30L, cart.getSubtotal());
            assertEquals(20_001, cart.getTotalWeightGrams());
        };
        assertThrows(CouponExpiredException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
        verify.run();
        assertThrows(CouponNotApplicableException.class,
            () -> service.placeOrder("D", cart, minimum, Region.REMOTE, t));
        verify.run();
        assertThrows(IllegalArgumentException.class,
            () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
        verify.run();
    }
}
