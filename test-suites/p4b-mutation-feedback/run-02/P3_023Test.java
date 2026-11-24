package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void injectedCalculationFailureStopsCheckoutAndPreservesAllLines(boolean failPricing) {
        Product a = new Product("A", "A", 20, 10, 4);
        Product b = new Product("B", "B", 30, 20, 5);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        List<String> calls = new ArrayList<>();

        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 5;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                throw new UnsupportedOperationException();
            }
        });

        if (failPricing) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
            assertEquals(List.of("pricing"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
            assertEquals(List.of("pricing", "shipping"), calls);
        }
        assertEquals(4, a.getStock());
        assertEquals(5, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
    }
}
