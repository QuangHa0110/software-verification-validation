package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void injectedCalculationFailureStopsPlacementAndPreservesEveryLine(boolean pricingFails) {
        Product a = new Product("A", "A", 20, 1, 4);
        Product b = new Product("B", "B", 5, 7, 3);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                if (pricingFails) {
                    throw new ArithmeticException();
                }
                return 1;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                throw new UnsupportedOperationException();
            }
        });

        if (pricingFails) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
            assertEquals(List.of("pricing"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
            assertEquals(List.of("pricing", "shipping"), calls);
        }
        assertEquals(4, a.getStock());
        assertEquals(3, b.getStock());
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(3, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
    }
}
