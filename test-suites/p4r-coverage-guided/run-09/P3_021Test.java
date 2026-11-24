package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void orderServiceRequiresBothDependenciesAndValidatesHeaderBeforeCart() {
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new AssertionError();
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                throw new AssertionError();
            }
        };
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, shipping));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(pricing, null));
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (Cart cart : new Cart[]{null, new Cart()}) {
            for (String id : new String[]{null, "", " \t"}) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(id, cart, null, Region.SUBURB, time));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, time));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, time));
        }
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, time));
    }
}
