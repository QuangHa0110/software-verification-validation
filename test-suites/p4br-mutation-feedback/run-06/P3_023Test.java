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
    void standardPricingAndShippingFailuresLeaveAllStockAndCartLinesIntact(String scenario) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Product a = new Product("A", "A", 10, 10_000, 1);
        Product b = new Product("B", "B", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        switch (scenario) {
            case "expired" -> {
                Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 31, 0, t.toLocalDate().minusDays(1));
                assertThrows(CouponExpiredException.class, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, t));
            }
            case "belowMinimum" -> {
                Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 31, 0, t.toLocalDate());
                assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, t));
            }
            case "overweight" -> assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
            default -> throw new AssertionError(scenario);
        }
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
