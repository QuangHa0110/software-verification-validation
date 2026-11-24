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
    void injectedCalculationFailuresStopAtTheFailingServiceAndPreserveStock(boolean failPricing) {
        Product a = new Product("A", "A", 10, 3, 2);
        Product b = new Product("B", "B", 7, 4, 3);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 3L;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                calls.add("shipping");
                throw new UnsupportedOperationException();
            }
        });
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
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
    }
}
