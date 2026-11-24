package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void testPricingServiceCalculateDiscountFixedCalculations() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon fixedCoupon = new Coupon("F400", CouponType.FIXED, 400L, 0L, 10L, today);
        assertEquals(300L, pricing.calculateDiscount(300L, fixedCoupon, today));
        assertEquals(0L, pricing.calculateDiscount(0L, fixedCoupon, today));
        assertEquals(400L, pricing.calculateDiscount(500L, fixedCoupon, today));
    }
}
