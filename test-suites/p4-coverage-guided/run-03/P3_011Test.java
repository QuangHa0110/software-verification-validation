package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void pricingValidatesArgumentsBeforeCouponAndExpiryBeforeMinimum() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 15, 99_999, 0, expiry);
        PricingService pricing = new PricingService();
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, expiry));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100_000, coupon, null));
        assertThrows(IllegalArgumentException.class,
                () -> pricing.calculateDiscount(-1, coupon, expiry.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, expiry));
        assertEquals(0L, pricing.calculateDiscount(123, null, expiry));
        assertThrows(CouponExpiredException.class,
                () -> pricing.calculateDiscount(99_998, coupon, expiry.plusDays(1)));
        assertThrows(CouponExpiredException.class,
                () -> pricing.calculateDiscount(100_000, coupon, expiry.plusDays(1)));
        assertThrows(CouponNotApplicableException.class,
                () -> pricing.calculateDiscount(99_998, coupon, expiry));
        assertEquals(14_999L, pricing.calculateDiscount(99_999, coupon, expiry));
        assertEquals(15_000L, pricing.calculateDiscount(100_000, coupon, expiry.minusDays(1)));
    }
}
