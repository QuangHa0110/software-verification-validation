package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void pricingValidatesInputsBeforeNullCouponAndExpiryBeforeMinimum() {
        PricingService pricing = new PricingService();
        LocalDate date = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 7, 100, 0, date);
        for (Coupon candidate : Arrays.asList(null, coupon)) {
            assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, candidate, date));
            assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100, candidate, null));
        }
        assertEquals(0L, pricing.calculateDiscount(0, null, date));
        assertEquals(0L, pricing.calculateDiscount(123, null, date));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(99, coupon, date.plusDays(1)));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(100, coupon, date.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99, coupon, date));
        assertEquals(7L, pricing.calculateDiscount(100, coupon, date));
        assertEquals(7L, pricing.calculateDiscount(101, coupon, date.minusDays(1)));
    }
}
