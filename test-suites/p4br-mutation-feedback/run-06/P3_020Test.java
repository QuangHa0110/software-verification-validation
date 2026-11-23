package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void placementValidatesHeaderThenCartThenAllStockBeforeCallingEitherCalculator() {
        Product a = new Product("A", "A", 10, 1, 1);
        Product b = new Product("B", "B", 20, 1, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon expired = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate().minusDays(1));
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                throw new AssertionError("Unexpected pricing call");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                throw new AssertionError("Unexpected shipping call");
            }
        });

        for (Cart invalidCart : Arrays.asList(null, new Cart())) {
            for (String blank : Arrays.asList(null, "", "\t ")) {
                assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder(blank, invalidCart, expired, Region.REMOTE, t));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", invalidCart, expired, null, t));
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", invalidCart, expired, Region.REMOTE, null));
            assertThrows(IllegalStateException.class,
                () -> service.placeOrder("D", invalidCart, expired, Region.REMOTE, t));
        }
        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
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
