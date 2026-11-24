package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void pricingServiceCalculateDiscountFixedCalculations() {
        PricingService pricingService = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon c1 = new Coupon("FIXED1", CouponType.FIXED, 400L, 0L, 1L, expiry);
        assertEquals(300L, pricingService.calculateDiscount(300L, c1, today));
        assertEquals(0L, pricingService.calculateDiscount(0L, c1, today));

        Coupon c2 = new Coupon("FIXED2", CouponType.FIXED, 200L, 0L, 0L, expiry);
        assertEquals(200L, pricingService.calculateDiscount(500L, c2, today));
        assertEquals(200L, pricingService.calculateDiscount(200L, c2, today));
    }
}
