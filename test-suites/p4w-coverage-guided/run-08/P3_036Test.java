package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void pricingServiceCalculateDiscountPercentCalculationsAndCap() {
        PricingService pricingService = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon c1 = new Coupon("C1", CouponType.PERCENT, 15L, 99999L, 10000L, expiry);
        assertEquals(10000L, pricingService.calculateDiscount(99999L, c1, today));

        Coupon c2 = new Coupon("C2", CouponType.PERCENT, 15L, 99999L, 0L, expiry);
        assertEquals(14999L, pricingService.calculateDiscount(99999L, c2, today));

        Coupon c3 = new Coupon("C3", CouponType.PERCENT, 10L, 0L, 10000L, expiry);
        assertEquals(10000L, pricingService.calculateDiscount(100000L, c3, today));

        Coupon c4 = new Coupon("C4", CouponType.PERCENT, 1L, 0L, 0L, expiry);
        assertEquals(0L, pricingService.calculateDiscount(7L, c4, today));

        Coupon c5 = new Coupon("C5", CouponType.PERCENT, 15L, 99999L, 15000L, expiry);
        assertEquals(14999L, pricingService.calculateDiscount(99999L, c5, today));
    }
}
