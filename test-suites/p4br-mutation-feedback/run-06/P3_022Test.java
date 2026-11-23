package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void injectedFailureStopsPlacementBeforeAnyStockDeduction(boolean failPricing) {
        Product a = new Product("A", "A", 30, 10, 2);
        Product b = new Product("B", "B", 40, 20, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                calls.add("pricing");
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                assertNull(coupon);
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                throw new UnsupportedOperationException();
            }
        });
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        if (failPricing) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
            assertEquals(List.of("pricing"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
            assertEquals(List.of("pricing", "shipping"), calls);
        }
        assertEquals(2, a.getStock());
        assertEquals(3, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
