package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void injectedCalculationFailureStopsPlacementWithoutChangingAnyLine(boolean failPricing) {
        Product a = new Product("A", "A", 10, 1, 3);
        Product b = new Product("B", "B", 20, 2, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                calls.add("discount");
                assertNull(coupon);
                if (failPricing) {
                    throw new ArithmeticException();
                }
                return 1;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                assertEquals(79L, goods);
                throw new UnsupportedOperationException();
            }
        });
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        org.junit.jupiter.api.function.Executable place =
            () -> service.placeOrder("D", cart, null, Region.SUBURB, t);
        if (failPricing) {
            assertThrows(ArithmeticException.class, place);
            assertEquals(List.of("discount"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class, place);
            assertEquals(List.of("discount", "shipping"), calls);
        }
        assertEquals(3, a.getStock());
        assertEquals(4, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
    }
}
