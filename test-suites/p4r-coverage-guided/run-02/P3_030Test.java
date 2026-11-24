package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @ParameterizedTest
    @ValueSource(strings = {"stock", "expired", "minimum", "weight"})
    void defaultPlacementStopsAtFirstFailureWithoutChangingAnyProductOrCartLine(String failure) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Product a = new Product("A", "A", 10, 10000, 1);
        Product b = new Product("B", "B", 20, 10001, 1);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        boolean expired = failure.equals("stock") || failure.equals("expired");
        LocalDate expiry = expired ? time.toLocalDate().minusDays(1) : time.toLocalDate();
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, failure.equals("weight") ? 30 : 31, 0, expiry);
        if (failure.equals("stock")) {
            b.reduceStock(1);
        }
        Class<? extends RuntimeException> expected = switch (failure) {
            case "stock" -> InsufficientStockException.class;
            case "expired" -> CouponExpiredException.class;
            case "minimum" -> CouponNotApplicableException.class;
            default -> IllegalArgumentException.class;
        };
        OrderService service = new OrderService();
        assertThrows(expected, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, time));
        assertEquals(1, a.getStock());
        assertEquals(failure.equals("stock") ? 0 : 1, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
