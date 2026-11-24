package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void placementFailuresCheckAllStockBeforeCouponAndCouponBeforeShipping() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "A", 10, 10001, 1);
        Product b = new Product("B", "B", 10, 5000, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        Coupon expired = new Coupon("X", CouponType.FIXED, 1, 31, 0, t.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 31, 0, t.toLocalDate());
        OrderService service = new OrderService();
        java.util.function.IntConsumer verify = expectedB -> {
            assertEquals(1, a.getStock());
            assertEquals(expectedB, b.getStock());
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
        };

        b.reduceStock(1);
        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
        verify.accept(1);
        b.restoreStock(1);
        assertThrows(CouponExpiredException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
        verify.accept(2);
        assertThrows(CouponNotApplicableException.class,
            () -> service.placeOrder("D", cart, minimum, Region.REMOTE, t));
        verify.accept(2);
        assertThrows(IllegalArgumentException.class,
            () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
        verify.accept(2);
    }
}
