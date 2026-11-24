package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_087Test {

    @Test
    void orderServicePlaceOrderFailsDuringPricingOrShippingAtomicity() {
        PricingService throwingPricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new CouponNotApplicableException("Custom fail");
            }
        };
        ShippingCalculator throwingShipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                throw new IllegalArgumentException("Custom shipping fail");
            }
        };

        Product p = new Product("P1", "Bút", 1000L, 10, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        OrderService service1 = new OrderService(throwingPricing, new ShippingCalculator());
        assertThrows(CouponNotApplicableException.class, () ->
            service1.placeOrder("D1", cart, null, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());

        OrderService service2 = new OrderService(new PricingService(), throwingShipping);
        assertThrows(IllegalArgumentException.class, () ->
            service2.placeOrder("D1", cart, null, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());
    }
}
