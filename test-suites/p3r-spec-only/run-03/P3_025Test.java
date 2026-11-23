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
    void injectedCalculationFailurePropagatesWithoutChangingAnyCartLineOrStock(boolean failPricing) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "First", 20, 2, 5);
        Product b = new Product("B", "Second", 30, 3, 7);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        List<String> calls = new ArrayList<>();

        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("discount");
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 3;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                throw new UnsupportedOperationException();
            }
        });

        if (failPricing) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
            assertEquals(List.of("discount"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
            assertEquals(List.of("discount", "shipping"), calls);
        }
        assertEquals(5, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(130L, cart.getSubtotal());
        assertEquals(13, cart.getTotalWeightGrams());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
    }
}
