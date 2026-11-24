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
    void injectedCalculationFailureStopsPlacementAtFailingStepWithoutChangingCartOrStock(
            boolean failPricing) {
        Product a = new Product("A", "A", 12, 4, 2);
        Product b = new Product("B", "B", 8, 5, 4);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 3);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                if (failPricing) throw new ArithmeticException();
                return 2;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                calls.add("shipping");
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
        assertEquals(4, b.getStock());
        List<CartItem> rows = cart.getItems();
        assertEquals(2, rows.size());
        assertSame(a, rows.get(0).getProduct());
        assertEquals(1, rows.get(0).getQuantity());
        assertSame(b, rows.get(1).getProduct());
        assertEquals(3, rows.get(1).getQuantity());
    }
}
