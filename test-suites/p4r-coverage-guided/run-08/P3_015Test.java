package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void pricingValidatesSubtotalAndDateBeforeCouponAndExpiryBeforeMinimum() {
        PricingService pricing = new PricingService();
        LocalDate day = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 15, 99_999, 0, day);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, day));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(99_999, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, coupon, day.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, day));
        assertEquals(0L, pricing.calculateDiscount(100_000, null, day));
        assertThrows(CouponExpiredException.class,
            () -> pricing.calculateDiscount(99_998, coupon, day.plusDays(1)));
        assertThrows(CouponNotApplicableException.class,
            () -> pricing.calculateDiscount(99_998, coupon, day));
        assertEquals(14_999L, pricing.calculateDiscount(99_999, coupon, day));
        assertEquals(15_000L, pricing.calculateDiscount(100_000, coupon, day.minusDays(1)));
    }
}
