package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void defaultPlacementChecksCouponBeforeOverweightShippingAndPreservesInventoryOnFailure() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 12, 0);
        Product a = new Product("A", "A", 250_000, 10_000, 2);
        Product b = new Product("B", "B", 250_000, 10_001, 3);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 500_001, 0, t.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 500_001, 0, t.toLocalDate());
        Runnable check = () -> {
            assertEquals(2, a.getStock());
            assertEquals(3, b.getStock());
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(1, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(1, items.get(1).getQuantity());
        };
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D", cart, expired, Region.INNER_CITY, t));
        check.run();
        assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D", cart, minimum, Region.INNER_CITY, t));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.INNER_CITY, t));
        check.run();
    }
}
