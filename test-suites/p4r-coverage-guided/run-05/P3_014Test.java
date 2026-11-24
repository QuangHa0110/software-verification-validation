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
        PricingService pricing = new PricingService();
        LocalDate end = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 10, 100, 0, end);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, end));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100, coupon, null));
        assertEquals(0L, pricing.calculateDiscount(0, null, end));
        assertEquals(0L, pricing.calculateDiscount(100, null, end));
        assertThrows(CouponExpiredException.class,
                () -> pricing.calculateDiscount(99, coupon, end.plusDays(1)));
        assertThrows(CouponExpiredException.class,
                () -> pricing.calculateDiscount(100, coupon, end.plusDays(1)));
        assertThrows(CouponNotApplicableException.class,
                () -> pricing.calculateDiscount(99, coupon, end));
        assertEquals(10L, pricing.calculateDiscount(100, coupon, end));
        assertEquals(10L, pricing.calculateDiscount(101, coupon, end.minusDays(1)));
    }
}
