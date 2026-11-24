package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @ParameterizedTest
    @ValueSource(strings = {"stock", "expiry", "minimum", "weight"})
    void placementFailuresRespectPrecedenceAndLeaveEveryProductAndLineIntact(String failure) {
        Product a = new Product("A", "A", 10, 10_000, 1);
        Product b = new Product("B", "B", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        LocalDate day = t.toLocalDate();
        if ("stock".equals(failure)) {
            b.reduceStock(2);
        }
        Coupon coupon = switch (failure) {
            case "stock", "expiry" -> new Coupon("E", CouponType.FIXED, 1, 31, 0, day.minusDays(1));
            case "minimum" -> new Coupon("M", CouponType.FIXED, 1, 31, 0, day);
            default -> null;
        };
        Class<? extends RuntimeException> expected = switch (failure) {
            case "stock" -> InsufficientStockException.class;
            case "expiry" -> CouponExpiredException.class;
            case "minimum" -> CouponNotApplicableException.class;
            default -> IllegalArgumentException.class;
        };
        OrderService service = new OrderService();
        assertThrows(expected, () -> service.placeOrder("O", cart, coupon, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals("stock".equals(failure) ? 0 : 2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
        assertEquals(30L, cart.getSubtotal());
        assertEquals(20_001, cart.getTotalWeightGrams());
    }
}
