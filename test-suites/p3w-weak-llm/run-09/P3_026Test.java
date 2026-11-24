package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void orderServiceConstructorsAndDependencyInjection() {
        assertDoesNotThrow(() -> new OrderService());

        PricingService ps = new PricingService();
        ShippingCalculator sc = new ShippingCalculator();

        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, sc));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(ps, null));
        assertDoesNotThrow(() -> new OrderService(ps, sc));

        PricingService stubPs = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                return 7L;
            }
        };
        ShippingCalculator stubSc = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                return 11L;
            }
        };

        OrderService customService = new OrderService(stubPs, stubSc);
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Name", 100L, 1, 5), 1);
        Order order = customService.placeOrder("D_CUSTOM", cart, null, Region.INNER_CITY, LocalDateTime.of(2026, 11, 23, 8, 0));

        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
    }
}
