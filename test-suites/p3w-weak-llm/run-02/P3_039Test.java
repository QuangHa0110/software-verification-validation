package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void orderServicePlaceOrderWithInjectedCustomServices() {
        PricingService mockPricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                return 7L;
            }
        };
        ShippingCalculator mockShipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                return 11L;
            }
        };

        OrderService service = new OrderService(mockPricing, mockShipping);
        Cart cart = new Cart();
        Product p = new Product("P", "Món", 100L, 1, 10);
        cart.addItem(p, 1);

        Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, LocalDateTime.of(2026, 11, 23, 8, 0));

        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
    }
}
