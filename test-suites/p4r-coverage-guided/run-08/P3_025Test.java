package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void injectedCalculationFailureStopsPlacementAndLeavesEveryLineUntouched(boolean failPricing) {
        Product a = new Product("A", "First", 10, 3, 3);
        Product b = new Product("B", "Second", 7, 1, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                calls.add("pricing");
                assertNull(coupon);
                assertEquals(3, a.getStock());
                assertEquals(2, b.getStock());
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 2;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(25L, goods);
                assertEquals(3, a.getStock());
                assertEquals(2, b.getStock());
                throw new UnsupportedOperationException();
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Class<? extends RuntimeException> expected =
            failPricing ? ArithmeticException.class : UnsupportedOperationException.class;
        assertThrows(expected, () -> service.placeOrder("D", cart, null, Region.SUBURB, t));
        assertEquals(failPricing ? List.of("pricing") : List.of("pricing", "shipping"), calls);
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
