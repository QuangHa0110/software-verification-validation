package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @ValueSource(strings = {"stock", "expired", "minimum", "weight"})
    void defaultPlacementChecksAllStockThenCouponThenWeightAtomically(String failure) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 0, 0);
        Product a = new Product("A", "A", 10, 10001, 1);
        Product b = new Product("B", "B", 20, 5000, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        OrderService service = new OrderService();
        LocalDate today = time.toLocalDate();
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 51, 0, today.minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 51, 0, today);
        switch (failure) {
            case "stock" -> {
                b.reduceStock(1);
                assertThrows(InsufficientStockException.class,
                        () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
            }
            case "expired" -> assertThrows(CouponExpiredException.class,
                    () -> service.placeOrder("D", cart, expired, Region.REMOTE, time));
            case "minimum" -> assertThrows(CouponNotApplicableException.class,
                    () -> service.placeOrder("D", cart, minimum, Region.REMOTE, time));
            case "weight" -> assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
            default -> throw new AssertionError();
        }
        assertEquals(1, a.getStock());
        assertEquals(failure.equals("stock") ? 1 : 2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(50L, cart.getSubtotal());
        assertEquals(20001, cart.getTotalWeightGrams());
    }
}
