package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void stockCouponAndShippingFailuresTakePriorityInThatOrderAndLeaveCheckoutAtomic() {
        Product a = new Product("A", "A", 10, 10_000, 1);
        Product b = new Product("B", "B", 20, 10_001, 1);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        b.reduceStock(1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 31, 0, time.toLocalDate().minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 31, 0, time.toLocalDate());
        OrderService service = new OrderService();

        Runnable verifyCart = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
            assertEquals(30L, cart.getSubtotal());
            assertEquals(20_001, cart.getTotalWeightGrams());
            assertEquals(1, a.getStock());
        };

        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
        verifyCart.run();
        assertEquals(0, b.getStock());
        b.restoreStock(1);

        assertThrows(CouponExpiredException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
        verifyCart.run();
        assertEquals(1, b.getStock());

        assertThrows(CouponNotApplicableException.class,
            () -> service.placeOrder("D", cart, minimum, Region.REMOTE, time));
        verifyCart.run();
        assertEquals(1, b.getStock());

        assertThrows(IllegalArgumentException.class,
            () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
        verifyCart.run();
        assertEquals(1, b.getStock());
    }
}
