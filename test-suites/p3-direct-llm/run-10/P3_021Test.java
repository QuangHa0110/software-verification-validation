package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void orderServiceRejectsMissingDependenciesAndValidatesHeadersBeforeCart() {
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                throw new AssertionError("Unexpected pricing call");
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                throw new AssertionError("Unexpected shipping call");
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Cart empty = new Cart();
        for (Cart cart : Arrays.asList(null, empty)) {
            for (String blank : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder(blank, cart, null, Region.INNER_CITY, t));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, t));
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.INNER_CITY, null));
            assertThrows(IllegalStateException.class,
                () -> service.placeOrder("D", cart, null, Region.INNER_CITY, t));
        }
        assertTrue(empty.isEmpty());
    }
}
