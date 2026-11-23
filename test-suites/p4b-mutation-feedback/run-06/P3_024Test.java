package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void orderServiceRequiresBothDependenciesAndValidatesHeaderBeforeCart() {
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError("Pricing reached before validation");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                throw new AssertionError("Shipping reached before validation");
            }
        });
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (Cart cart : Arrays.asList(null, new Cart())) {
            for (String id : Arrays.asList(null, "", " \t")) {
                assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder(id, cart, null, Region.SUBURB, time));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, time));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
        }
    }
}
