package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void pricingServiceFixedDiscountCalculations() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);

        Coupon fixedCoupon = new Coupon("F1", CouponType.FIXED, 400L, 0L, 1L, expiry);
        assertEquals(300L, pricing.calculateDiscount(300L, fixedCoupon, expiry));
        assertEquals(0L, pricing.calculateDiscount(0L, fixedCoupon, expiry));
        assertEquals(400L, pricing.calculateDiscount(500L, fixedCoupon, expiry));
        assertEquals(400L, pricing.calculateDiscount(400L, fixedCoupon, expiry));
    }
}
