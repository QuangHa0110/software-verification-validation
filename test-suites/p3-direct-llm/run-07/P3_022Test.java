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
    void calculatorExceptionStopsCheckoutAtFailingCallAndPreservesBothLines(boolean pricingFails) {
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                if (pricingFails) {
                    throw new ArithmeticException();
                }
                return 0;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                throw new UnsupportedOperationException();
            }
        };
        Product a = new Product("A", "First", 3, 2, 5);
        Product b = new Product("B", "Second", 7, 3, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        if (pricingFails) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
            assertEquals(List.of("pricing"), calls);
        } else {
            assertThrows(UnsupportedOperationException.class,
                    () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
            assertEquals(List.of("pricing", "shipping"), calls);
        }
        assertEquals(5, a.getStock());
        assertEquals(4, b.getStock());
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(2, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(3, items.get(1).getQuantity());
    }
}
