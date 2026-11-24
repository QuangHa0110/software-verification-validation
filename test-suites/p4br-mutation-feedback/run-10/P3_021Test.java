package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void invalidOrderHeaderCartAndStockStopBeforePricingOrShipping() {
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
        for (Cart invalidCart : Arrays.asList(null, cart)) {
            for (String id : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(id, invalidCart, null, r, t));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", invalidCart, null, null, t));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", invalidCart, null, r, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", invalidCart, null, r, t));
        }
        assertTrue(cart.isEmpty());
        Product a = new Product("A", "first", 10, 1, 1);
        Product b = new Product("B", "second", 20, 1, 2);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Coupon expired = new Coupon("X", CouponType.FIXED, 1, 100, 0, t.toLocalDate().minusDays(1));
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, expired, r, t));
        assertEquals(1, a.getStock());
        assertEquals(1, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
    }
}
