package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @ParameterizedTest
    @ValueSource(strings = {"expired", "minimum", "overweight"})
    void standardPricingAndShippingFailuresLeaveBothStocksAndCartIntact(String failure) {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Product a = new Product("A", "first", 10, 10_000, 1);
        Product b = new Product("B", "second", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        if (failure.equals("overweight")) {
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
        } else {
            boolean expired = failure.equals("expired");
            LocalDate expiry = expired ? t.toLocalDate().minusDays(1) : t.toLocalDate();
            Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 31, 0, expiry);
            Class<? extends RuntimeException> expected = expired
                    ? CouponExpiredException.class : CouponNotApplicableException.class;
            assertThrows(expected, () -> service.placeOrder("D", cart, coupon, Region.REMOTE, t));
        }
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
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
