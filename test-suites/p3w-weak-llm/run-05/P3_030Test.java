package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void testPricingServiceCalculateDiscountPercentCalculations() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon cNoCap = new Coupon("P15", CouponType.PERCENT, 15L, 0L, 0L, today);
        assertEquals(14999L, pricing.calculateDiscount(99999L, cNoCap, today));

        Coupon cCap = new Coupon("P15_CAP", CouponType.PERCENT, 15L, 0L, 10000L, today);
        assertEquals(10000L, pricing.calculateDiscount(99999L, cCap, today));

        Coupon cCapExact = new Coupon("P10", CouponType.PERCENT, 10L, 0L, 10000L, today);
        assertEquals(10000L, pricing.calculateDiscount(100000L, cCapExact, today));

        Coupon cSmall = new Coupon("P1", CouponType.PERCENT, 1L, 0L, 0L, today);
        assertEquals(0L, pricing.calculateDiscount(7L, cSmall, today));
    }
}
