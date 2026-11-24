package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void pricingServicePercentCalculationsAndCaps() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);

        Coupon cNoCap = new Coupon("P1", CouponType.PERCENT, 15L, 99999L, 0L, expiry);
        assertEquals(14999L, pricing.calculateDiscount(99999L, cNoCap, expiry));

        Coupon cWithCapExceeded = new Coupon("P2", CouponType.PERCENT, 15L, 99999L, 10000L, expiry);
        assertEquals(10000L, pricing.calculateDiscount(99999L, cWithCapExceeded, expiry));

        Coupon cWithCapExact = new Coupon("P3", CouponType.PERCENT, 10L, 0L, 10000L, expiry);
        assertEquals(10000L, pricing.calculateDiscount(100000L, cWithCapExact, expiry));

        Coupon cWithCapUnder = new Coupon("P4", CouponType.PERCENT, 15L, 99999L, 15000L, expiry);
        assertEquals(14999L, pricing.calculateDiscount(99999L, cWithCapUnder, expiry));

        Coupon cSmall = new Coupon("P5", CouponType.PERCENT, 1L, 0L, 0L, expiry);
        assertEquals(0L, pricing.calculateDiscount(7L, cSmall, expiry));
    }
}
