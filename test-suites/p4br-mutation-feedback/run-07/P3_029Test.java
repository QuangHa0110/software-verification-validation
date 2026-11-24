package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @ValueSource(strings = {"stock", "expired", "minimum", "weight"})
    void placementStopsAtFirstFailingStageAndPreservesBothProductsAndCart(String scenario) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0);
        Product a = new Product("A", "First", 10, 10_001, 1);
        Product b = new Product("B", "Second", 20, 5_000, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        if (scenario.equals("stock")) {
            b.reduceStock(1);
        }
        LocalDate expiry = scenario.equals("stock") || scenario.equals("expired")
            ? t.toLocalDate().minusDays(1) : t.toLocalDate();
        long minimum = scenario.equals("weight") ? 50 : 51;
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, minimum, 0, expiry);
        Class<? extends RuntimeException> expected = switch (scenario) {
            case "stock" -> InsufficientStockException.class;
            case "expired" -> CouponExpiredException.class;
            case "minimum" -> CouponNotApplicableException.class;
            case "weight" -> IllegalArgumentException.class;
            default -> throw new AssertionError();
        };
        OrderService service = new OrderService();
        assertThrows(expected, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, t));
        assertEquals(1, a.getStock());
        assertEquals(scenario.equals("stock") ? 1 : 2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(50L, cart.getSubtotal());
        assertEquals(20_001, cart.getTotalWeightGrams());
    }
}
