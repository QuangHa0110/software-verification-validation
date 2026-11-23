package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void orderServiceRejectsMissingDependenciesAndValidatesHeaderBeforeCartBeforePricing() {
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate date) {
                throw new AssertionError("Unexpected pricing call");
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                throw new AssertionError("Unexpected shipping call");
            }
        });
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.SUBURB;
        Cart empty = new Cart();
        for (Cart invalidCart : new Cart[]{null, empty}) {
            for (String id : new String[]{null, "", " \t"}) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(id, invalidCart, null, r, t));
            }
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("O", invalidCart, null, null, t));
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("O", invalidCart, null, r, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("O", invalidCart, null, r, t));
        }
        assertTrue(empty.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, t));
    }
}
