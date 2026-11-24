package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void pricingServiceCalculateDiscountPercentCalculationsAndCaps() {
        PricingService ps = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon cNoCap = new Coupon("C1", CouponType.PERCENT, 15L, 0L, 0L, today);
        assertEquals(14999L, ps.calculateDiscount(99999L, cNoCap, today));

        Coupon cWithCap = new Coupon("C2", CouponType.PERCENT, 15L, 0L, 10000L, today);
        assertEquals(10000L, ps.calculateDiscount(99999L, cWithCap, today));

        Coupon cExactCap = new Coupon("C3", CouponType.PERCENT, 10L, 0L, 10000L, today);
        assertEquals(10000L, ps.calculateDiscount(100000L, cExactCap, today));

        Coupon cBelowCap = new Coupon("C4", CouponType.PERCENT, 15L, 0L, 20000L, today);
        assertEquals(14999L, ps.calculateDiscount(99999L, cBelowCap, today));

        Coupon cSmall = new Coupon("C5", CouponType.PERCENT, 1L, 0L, 0L, today);
        assertEquals(0L, ps.calculateDiscount(7L, cSmall, today));
    }
}
