package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void pricingChecksInputsThenExpiryThenMinimumBeforeComputingDiscount() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 10, 200, 0, expiry);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, expiry));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(200, coupon, null));
        assertEquals(0L, pricing.calculateDiscount(0, null, expiry));
        assertEquals(0L, pricing.calculateDiscount(200, null, expiry));

        assertThrows(CouponExpiredException.class,
            () -> pricing.calculateDiscount(199, coupon, expiry.plusDays(1)));
        assertThrows(CouponExpiredException.class,
            () -> pricing.calculateDiscount(200, coupon, expiry.plusDays(1)));
        assertThrows(CouponNotApplicableException.class,
            () -> pricing.calculateDiscount(199, coupon, expiry));
        assertEquals(20L, pricing.calculateDiscount(200, coupon, expiry));
        assertEquals(21L, pricing.calculateDiscount(210, coupon, expiry.minusDays(1)));
        assertEquals(20L, pricing.calculateDiscount(200, coupon, expiry));
    }
}
