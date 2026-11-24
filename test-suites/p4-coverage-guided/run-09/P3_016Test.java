package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void orderServiceValidatesDependenciesHeaderCartAndStockBeforeCallingCalculators() {
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError();
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                throw new AssertionError();
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.INNER_CITY;
        Cart cart = new Cart();
        for (String id : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder(id, cart, null, r, t));
        }
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", null, null, null, t));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, r, null));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", null, null, r, t));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, r, t));

        Product a = new Product("A", "A", 10, 1, 1);
        Product b = new Product("B", "B", 20, 1, 2);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 0, 0, t.toLocalDate().minusDays(1));
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, expired, r, t));
        assertEquals(1, a.getStock());
        assertEquals(1, b.getStock());
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(1, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
    }
}
