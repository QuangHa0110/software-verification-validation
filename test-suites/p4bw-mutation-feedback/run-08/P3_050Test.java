package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_050Test {

    @Test
    void orderServicePlaceOrderCustomServicesInjected() {
        PricingService dummyPricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                return 7L;
            }
        };
        ShippingCalculator dummyShipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                return 11L;
            }
        };
        OrderService service = new OrderService(dummyPricing, dummyShipping);

        Cart cart = new Cart();
        Product p = new Product("P", "Item", 100L, 1, 5);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = service.placeOrder("D_CUSTOM", cart, null, Region.INNER_CITY, now);
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(4, p.getStock());
    }
}
