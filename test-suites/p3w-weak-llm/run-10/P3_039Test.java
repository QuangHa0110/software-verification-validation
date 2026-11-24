package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void testPricingServiceCouponExpiredChecksBeforeMinOrder() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 15L, 99999L, 10000L, expiry);

        LocalDate nextDay = LocalDate.of(2026, 11, 24);
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(99998L, coupon, nextDay));

        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99998L, coupon, expiry));
    }
}
