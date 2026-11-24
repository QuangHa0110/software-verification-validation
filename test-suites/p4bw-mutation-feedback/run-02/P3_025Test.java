package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void pricingServicePercentCalculationsAndCaps() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon cCap = new Coupon("C1", CouponType.PERCENT, 15L, 99999L, 10000L, today);
        assertEquals(10000L, pricing.calculateDiscount(99999L, cCap, today));

        Coupon cNoCap = new Coupon("C2", CouponType.PERCENT, 15L, 99999L, 0L, today);
        assertEquals(14999L, pricing.calculateDiscount(99999L, cNoCap, today));

        Coupon cExact = new Coupon("C3", CouponType.PERCENT, 10L, 0L, 10000L, today);
        assertEquals(10000L, pricing.calculateDiscount(100000L, cExact, today));

        Coupon cSmall = new Coupon("C4", CouponType.PERCENT, 1L, 0L, 0L, today);
        assertEquals(0L, pricing.calculateDiscount(7L, cSmall, today));
    }
}
