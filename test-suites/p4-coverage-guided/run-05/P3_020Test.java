package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void orderServiceRejectsMissingDependenciesAndInvalidInputsBeforePricing() {
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError("Unexpected pricing call");
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                throw new AssertionError("Unexpected shipping call");
            }
        };
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, shipping));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(pricing, null));
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.INNER_CITY;
        Cart empty = new Cart();
        for (Cart cart : Arrays.asList(null, empty)) {
            for (String blank : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(blank, cart, null, region, time));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, time));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, region, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, region, time));
        }
        assertTrue(empty.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, time));
    }
}
