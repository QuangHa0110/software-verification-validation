package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void orderServiceRejectsMissingDependenciesAndValidatesHeaderBeforeCart() {
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
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
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Cart empty = new Cart();
        for (Cart cart : Arrays.asList(null, empty)) {
            for (String blank : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(blank, cart, null, Region.SUBURB, t));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, t));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, t));
        }
        assertTrue(empty.isEmpty());
    }
}
