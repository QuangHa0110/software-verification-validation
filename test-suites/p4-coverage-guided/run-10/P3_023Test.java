package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void injectedFailuresStopAtTheFailingCalculationAndPreserveCartAndStock() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (boolean failPricing : List.of(true, false)) {
            Product a = new Product("A", "A", 8, 3, 4);
            Product b = new Product("B", "B", 5, 7, 3);
            Cart cart = new Cart();
            cart.addItem(a, 2);
            cart.addItem(b, 3);
            List<String> calls = new ArrayList<>();
            PricingService pricing = new PricingService() {
                @Override
                public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                    calls.add("pricing");
                    if (failPricing) {
                        throw new ArithmeticException();
                    }
                    return 1;
                }
            };
            ShippingCalculator shipping = new ShippingCalculator() {
                @Override
                public long calculate(Region region, int weight, long goods) {
                    calls.add("shipping");
                    throw new UnsupportedOperationException();
                }
            };
            OrderService service = new OrderService(pricing, shipping);
            if (failPricing) {
                assertThrows(ArithmeticException.class,
                    () -> service.placeOrder("D", cart, null, Region.PROVINCE, t));
                assertEquals(List.of("pricing"), calls);
            } else {
                assertThrows(UnsupportedOperationException.class,
                    () -> service.placeOrder("D", cart, null, Region.PROVINCE, t));
                assertEquals(List.of("pricing", "shipping"), calls);
            }
            assertEquals(4, a.getStock());
            assertEquals(3, b.getStock());
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
        }
    }
}
