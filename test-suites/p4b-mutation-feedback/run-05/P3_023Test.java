package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void placementChecksCurrentStockThenCouponThenWeightWithoutPartialWrites() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Product a = new Product("A", "First", 10, 1, 1);
        Product b = new Product("B", "Second", 20, 10_000, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 51, 0, time.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 51, 0, time.toLocalDate());
        OrderService service = new OrderService();
        java.util.function.IntConsumer verify = expectedBStock -> {
            assertEquals(1, a.getStock());
            assertEquals(expectedBStock, b.getStock());
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(50L, cart.getSubtotal());
            assertEquals(20_001, cart.getTotalWeightGrams());
        };
        b.reduceStock(1);
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
        verify.accept(1);
        b.restoreStock(1);
        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
        verify.accept(2);
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D", cart, minimum, Region.REMOTE, time));
        verify.accept(2);
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
        verify.accept(2);
    }
}
