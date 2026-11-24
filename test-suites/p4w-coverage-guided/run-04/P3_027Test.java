package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void pricingServiceCalculateDiscountFixedCalculations() {
        PricingService ps = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon fixedCoupon = new Coupon("FIXED", CouponType.FIXED, 400L, 0L, 1L, today);
        assertEquals(300L, ps.calculateDiscount(300L, fixedCoupon, today));
        assertEquals(400L, ps.calculateDiscount(500L, fixedCoupon, today));
        assertEquals(0L, ps.calculateDiscount(0L, fixedCoupon, today));
    }
}
