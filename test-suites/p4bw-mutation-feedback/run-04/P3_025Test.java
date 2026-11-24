package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void pricingServiceCalculateDiscountExpirationAndMinOrderOrderOfChecks() {
        PricingService ps = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 10L, 100000L, 0L, expiry);

        LocalDate nextDay = expiry.plusDays(1);
        assertThrows(CouponExpiredException.class, () -> ps.calculateDiscount(50000L, coupon, nextDay));

        LocalDate sameDay = expiry;
        assertThrows(CouponNotApplicableException.class, () -> ps.calculateDiscount(99999L, coupon, sameDay));

        long discount = ps.calculateDiscount(100000L, coupon, sameDay);
        assertEquals(10000L, discount);
    }
}
