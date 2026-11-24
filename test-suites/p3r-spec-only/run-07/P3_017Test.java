package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void pricingValidatesSubtotalAndDateBeforeNoCouponAndExpiryBeforeMinimum() {
        PricingService pricing = new PricingService();
        LocalDate d = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P", CouponType.PERCENT, 10, 100, 0, d);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, d));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(99, coupon, null));
        assertThrows(IllegalArgumentException.class,
            () -> pricing.calculateDiscount(-1, coupon, d.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, d));
        assertEquals(0L, pricing.calculateDiscount(100, null, d));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(99, coupon, d.plusDays(1)));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(100, coupon, d.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99, coupon, d));
        assertEquals(10L, pricing.calculateDiscount(100, coupon, d));
        assertEquals(12L, pricing.calculateDiscount(120, coupon, d.minusDays(1)));
    }
}
