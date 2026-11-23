package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void placementValidatesHeaderThenCartThenCurrentStockBeforeCallingEitherCalculator() {
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError("Unexpected discount calculation");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                throw new AssertionError("Unexpected shipping calculation");
            }
        });
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Cart cart = new Cart();
        Region region = Region.INNER_CITY;
        for (String blank : new String[] {null, "", "\t "}) {
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder(blank, cart, null, region, t));
        }
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", null, null, null, t));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, region, null));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", null, null, region, t));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, region, t));

        Product a = new Product("A", "A", 10, 1, 1);
        Product b = new Product("B", "B", 20, 1, 2);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 0, 0, t.toLocalDate().minusDays(1));
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, expired, region, t));
        assertEquals(1, a.getStock());
        assertEquals(1, b.getStock());
        assertEquals(List.of(a, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 2), cart.getItems().stream().map(CartItem::getQuantity).toList());
    }
}
