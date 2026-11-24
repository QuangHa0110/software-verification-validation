package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void orderServicePlaceOrderExceptionInPricingOrShippingRollsBackStock() {
        PricingService throwingPricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                throw new CouponNotApplicableException("test fail");
            }
        };
        OrderService service1 = new OrderService(throwingPricing, new ShippingCalculator());
        Product p1 = new Product("P1", "Pen", 10000L, 50, 5);
        Cart cart1 = new Cart();
        cart1.addItem(p1, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        assertThrows(CouponNotApplicableException.class, () -> service1.placeOrder("O1", cart1, null, Region.INNER_CITY, now));
        assertEquals(5, p1.getStock());

        OrderService service2 = new OrderService();
        Product heavy = new Product("P2", "Heavy", 10000L, 20001, 5);
        Cart cart2 = new Cart();
        cart2.addItem(heavy, 1);
        assertThrows(IllegalArgumentException.class, () -> service2.placeOrder("O2", cart2, null, Region.INNER_CITY, now));
        assertEquals(5, heavy.getStock());
    }
}
