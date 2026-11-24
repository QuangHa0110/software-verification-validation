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
    void injectedCalculationFailuresPropagateWithoutChangingCartOrStock(boolean pricingFails) {
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                calls.add("pricing");
                if (pricingFails) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                throw new NoSuchElementException();
            }
        });
        Product a = new Product("A", "First", 10, 7, 2);
        Product b = new Product("B", "Second", 20, 9, 5);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        if (pricingFails) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
            assertEquals(List.of("pricing"), calls);
        } else {
            assertThrows(NoSuchElementException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
            assertEquals(List.of("pricing", "shipping"), calls);
        }
        assertEquals(2, a.getStock());
        assertEquals(5, b.getStock());
        List<CartItem> rows = cart.getItems();
        assertEquals(2, rows.size());
        assertSame(a, rows.get(0).getProduct());
        assertEquals(2, rows.get(0).getQuantity());
        assertSame(b, rows.get(1).getProduct());
        assertEquals(3, rows.get(1).getQuantity());
    }
}
