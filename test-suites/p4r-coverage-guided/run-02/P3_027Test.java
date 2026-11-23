package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void placeOrderValidatesHeaderBeforeCartAndSkipsBothCalculatorsOnInvalidInput() {
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate day) {
                throw new AssertionError();
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                throw new AssertionError();
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Cart empty = new Cart();
        for (Cart cart : Arrays.asList(null, empty)) {
            for (String id : Arrays.asList(null, "", "\t ")) {
                assertThrows(IllegalArgumentException.class,
                    () -> service.placeOrder(id, cart, null, Region.REMOTE, time));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, time));
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, null));
            assertThrows(IllegalStateException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, time));
        }
        assertTrue(empty.isEmpty());
    }
}
