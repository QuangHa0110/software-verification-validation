package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void injectedCalculationFailuresStopAtFailingServiceAndLeaveStockAndCartIntact() {
        Product a = new Product("A", "First", 10, 3, 4);
        Product b = new Product("B", "Second", 20, 5, 2);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        for (boolean failPricing : new boolean[]{true, false}) {
            List<String> calls = new ArrayList<>();
            OrderService service = new OrderService(new PricingService() {
                @Override
                public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                    calls.add("pricing");
                    if (failPricing) {
                        throw new ArithmeticException();
                    }
                    return 0;
                }
            }, new ShippingCalculator() {
                @Override
                public long calculate(Region region, int weight, long goods) {
                    calls.add("shipping");
                    throw new NoSuchElementException();
                }
            });
            if (failPricing) {
                assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
                assertEquals(List.of("pricing"), calls);
            } else {
                assertThrows(NoSuchElementException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
                assertEquals(List.of("pricing", "shipping"), calls);
            }
            assertEquals(4, a.getStock());
            assertEquals(2, b.getStock());
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
    }
}
