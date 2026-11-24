package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void pricingValidatesInputsBeforeNoCouponAndExpiryBeforeMinimum() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        PricingService pricing = new PricingService();
        Coupon coupon = new Coupon("C", CouponType.FIXED, 7, 100, 0, expiry);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, expiry));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, coupon, expiry.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, expiry));
        assertEquals(0L, pricing.calculateDiscount(100, null, expiry));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(99, coupon, expiry.plusDays(1)));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(100, coupon, expiry.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99, coupon, expiry));
        assertEquals(7L, pricing.calculateDiscount(100, coupon, expiry));
        assertEquals(7L, pricing.calculateDiscount(101, coupon, expiry.minusDays(1)));
    }
}
