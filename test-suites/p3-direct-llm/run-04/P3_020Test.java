package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void placementChecksHeaderThenCartThenAllStocksBeforeCallingCalculators() {
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                throw new AssertionError("Unexpected pricing call");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                throw new AssertionError("Unexpected shipping call");
            }
        });
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.SUBURB;
        Cart empty = new Cart();
        for (Cart invalidCart : Arrays.asList(null, empty)) {
            for (String id : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder(id, invalidCart, null, region, time));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", invalidCart, null, null, time));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", invalidCart, null, region, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", invalidCart, null, region, time));
        }
        assertTrue(empty.isEmpty());

        Product a = new Product("A", "First", 10, 1, 1);
        Product b = new Product("B", "Second", 20, 1, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Coupon expired = new Coupon("C", CouponType.FIXED, 1, 0, 0, time.toLocalDate().minusDays(1));
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, expired, region, time));
        assertEquals(1, a.getStock());
        assertEquals(1, b.getStock());
        List<CartItem> rows = cart.getItems();
        assertEquals(2, rows.size());
        assertSame(a, rows.get(0).getProduct());
        assertEquals(1, rows.get(0).getQuantity());
        assertSame(b, rows.get(1).getProduct());
        assertEquals(2, rows.get(1).getQuantity());
    }
}
