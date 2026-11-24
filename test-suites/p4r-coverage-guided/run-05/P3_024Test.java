package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void placementValidatesHeaderThenCartThenEveryStockBeforeCallingEitherCalculator() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon expired = new Coupon("X", CouponType.FIXED, 1, 0, 0, t.toLocalDate().minusDays(1));
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError("Unexpected pricing call");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                throw new AssertionError("Unexpected shipping call");
            }
        });
        for (Cart empty : Arrays.asList(null, new Cart())) {
            for (String id : Arrays.asList(null, "", "\t ")) {
                assertThrows(IllegalArgumentException.class,
                        () -> service.placeOrder(id, empty, expired, Region.SUBURB, t));
            }
            assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder("D", empty, expired, null, t));
            assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder("D", empty, expired, Region.SUBURB, null));
            assertThrows(IllegalStateException.class,
                    () -> service.placeOrder("D", empty, expired, Region.SUBURB, t));
        }

        Product a = new Product("A", "A", 10, 1, 1);
        Product b = new Product("B", "B", 20, 1, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        assertThrows(InsufficientStockException.class,
                () -> service.placeOrder("D", cart, expired, Region.SUBURB, t));
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
