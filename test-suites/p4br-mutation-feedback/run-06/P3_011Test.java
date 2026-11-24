package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void pricingValidatesInputsBeforeNoCouponAndExpiryBeforeMinimum() {
        LocalDate day = LocalDate.of(2026, 11, 23);
        PricingService pricing = new PricingService();
        Coupon coupon = new Coupon("C", CouponType.FIXED, 20, 100, 0, day);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, day));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, coupon, day.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, day));
        assertEquals(0L, pricing.calculateDiscount(100, null, day));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(99, coupon, day.plusDays(1)));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(100, coupon, day.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99, coupon, day));
        assertEquals(20L, pricing.calculateDiscount(100, coupon, day));
        assertEquals(20L, pricing.calculateDiscount(101, coupon, day.minusDays(1)));
    }
}
