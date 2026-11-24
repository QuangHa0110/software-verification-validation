package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @ValueSource(strings = {"stock", "expired", "minimum", "overweight"})
    void standardPlacementFailuresFollowStockPricingShippingOrderAndPreserveCart(String failure) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        LocalDate day = time.toLocalDate();
        Product a = new Product("A", "A", 300_000, 10_000, 1);
        Product b = new Product("B", "B", 200_000, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 500_001, 0, day.minusDays(1));
        Coupon minimum = new Coupon("M", CouponType.FIXED, 1, 500_001, 0, day);

        switch (failure) {
            case "stock" -> {
                b.reduceStock(2);
                assertThrows(InsufficientStockException.class,
                    () -> service.placeOrder("D", cart, expired, Region.INNER_CITY, time));
            }
            case "expired" -> assertThrows(CouponExpiredException.class,
                () -> service.placeOrder("D", cart, expired, Region.INNER_CITY, time));
            case "minimum" -> assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D", cart, minimum, Region.INNER_CITY, time));
            case "overweight" -> assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.INNER_CITY, time));
            default -> fail(failure);
        }
        assertEquals(1, a.getStock());
        assertEquals(failure.equals("stock") ? 0 : 2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
        assertEquals(500_000L, cart.getSubtotal());
        assertEquals(20_001, cart.getTotalWeightGrams());
    }
}
