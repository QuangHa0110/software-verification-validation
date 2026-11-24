package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void placementChecksEveryStockBalanceBeforeCouponAndCouponBeforeOverweightShipping() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0);
        Product a = new Product("A", "A", 10, 10_001, 1);
        Product b = new Product("B", "B", 20, 5_000, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 51, 0, t.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 51, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Runnable verifyCart = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(50L, cart.getSubtotal());
            assertEquals(20_001, cart.getTotalWeightGrams());
        };

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("O", cart, expired, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(1, b.getStock());
        verifyCart.run();

        b.restoreStock(1);
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("O", cart, expired, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        verifyCart.run();
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("O", cart, minimum, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        verifyCart.run();
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("O", cart, null, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        verifyCart.run();
    }
}
