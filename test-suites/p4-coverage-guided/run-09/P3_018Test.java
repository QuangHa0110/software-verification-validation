package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void injectedCalculationFailuresStopPlacementAndLeaveEveryLineAndStockIntact() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (boolean failPricing : new boolean[] {true, false}) {
            Product a = new Product("A", "A", 10, 2, 4);
            Product b = new Product("B", "B", 20, 3, 3);
            Cart cart = new Cart();
            cart.addItem(a, 2);
            cart.addItem(b, 3);
            List<String> calls = new ArrayList<>();
            PricingService pricing = new PricingService() {
                @Override
                public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                    calls.add("discount");
                    if (failPricing) {
                        throw new ArithmeticException();
                    }
                    return 5;
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
                assertThrows(ArithmeticException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, t));
                assertEquals(List.of("discount"), calls);
            } else {
                assertThrows(UnsupportedOperationException.class,
                        () -> service.placeOrder("D", cart, null, Region.SUBURB, t));
                assertEquals(List.of("discount", "shipping"), calls);
            }
            assertEquals(4, a.getStock());
            assertEquals(3, b.getStock());
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(2, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(3, items.get(1).getQuantity());
        }
    }
}
