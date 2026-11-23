package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void standardPlacementRejectsExpiredBelowMinimumAndOverweightOrdersAtomically() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Product a = new Product("A", "First", 10, 10_000, 1);
        Product b = new Product("B", "Second", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Coupon expired = new Coupon("E", CouponType.PERCENT, 10, 31, 0, time.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 31, 0, time.toLocalDate());
        Runnable verify = () -> {
            assertEquals(1, a.getStock());
            assertEquals(2, b.getStock());
            List<CartItem> rows = cart.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(1, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(1, rows.get(1).getQuantity());
        };
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
        verify.run();
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D", cart, minimum, Region.REMOTE, time));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
        verify.run();
    }
}
