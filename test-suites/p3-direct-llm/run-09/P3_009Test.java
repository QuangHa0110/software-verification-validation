package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void pricingValidatesInputsBeforeCouponAndExpiryBeforeMinimum() {
        LocalDate date = LocalDate.of(2026, 11, 23);
        PricingService pricing = new PricingService();
        Coupon coupon = new Coupon("C", CouponType.FIXED, 101, 200, 0, date);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, date));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(200, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, coupon, date.plusDays(1)));
        assertEquals(0L, pricing.calculateDiscount(0, null, date));
        assertEquals(0L, pricing.calculateDiscount(999, null, date));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(199, coupon, date.plusDays(1)));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(200, coupon, date.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(199, coupon, date));
        assertEquals(101L, pricing.calculateDiscount(200, coupon, date));
        assertEquals(101L, pricing.calculateDiscount(201, coupon, date.minusDays(1)));
    }
}
