package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void placementValidatesHeaderBeforeCartAndNeverPricesAnInvalidRequest() {
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("discount");
                return 0;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                return 0;
            }
        });
        Cart empty = new Cart();
        Region r = Region.SUBURB;
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (Cart cart : new Cart[] {null, empty}) {
            for (String id : new String[] {null, "", " \t"}) {
                assertThrows(IllegalArgumentException.class, () -> service.placeOrder(id, cart, null, r, t));
            }
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, t));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, r, null));
            assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, r, t));
        }
        assertTrue(calls.isEmpty());
        assertTrue(empty.isEmpty());
    }
}
