package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @ValueSource(strings = {"expired", "belowMinimum", "overweight"})
    void standardCouponAndWeightFailuresLeaveEveryProductAndCartLineIntact(String scenario) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        LocalDate today = time.toLocalDate();
        Product a = new Product("A", "First", 10, 10_000, 1);
        Product b = new Product("B", "Second", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Coupon coupon = switch (scenario) {
            case "expired" -> new Coupon("C", CouponType.FIXED, 1, 31, 0, today.minusDays(1));
            case "belowMinimum" -> new Coupon("C", CouponType.FIXED, 1, 31, 0, today);
            default -> null;
        };
        Class<? extends RuntimeException> expected = switch (scenario) {
            case "expired" -> CouponExpiredException.class;
            case "belowMinimum" -> CouponNotApplicableException.class;
            default -> IllegalArgumentException.class;
        };
        assertThrows(expected, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, time));
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(1, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(1, items.get(1).getQuantity());
    }
}
