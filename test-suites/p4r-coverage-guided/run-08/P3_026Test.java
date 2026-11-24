package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @ParameterizedTest
    @ValueSource(strings = {"expired", "minimum", "weight"})
    void standardPlacementFailuresPreserveStockAndRespectCouponBeforeWeight(String scenario) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0);
        Product a = new Product("A", "First", 10, 10_000, 1);
        Product b = new Product("B", "Second", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        Coupon coupon = switch (scenario) {
            case "expired" -> new Coupon("E", CouponType.FIXED, 1, 31, 0, t.toLocalDate().minusDays(1));
            case "minimum" -> new Coupon("M", CouponType.FIXED, 1, 31, 0, t.toLocalDate());
            case "weight" -> null;
            default -> throw new AssertionError(scenario);
        };
        Class<? extends RuntimeException> expected = switch (scenario) {
            case "expired" -> CouponExpiredException.class;
            case "minimum" -> CouponNotApplicableException.class;
            default -> IllegalArgumentException.class;
        };
        OrderService service = new OrderService();
        assertThrows(expected, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
