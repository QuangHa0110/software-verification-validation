package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void pricingServiceCalculateDiscountExpiryAndMinOrderExceptions() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 10L, 50000L, 0L, expiry);

        LocalDate nextDay = LocalDate.of(2026, 11, 24);
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(40000L, coupon, nextDay));
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(60000L, coupon, nextDay));

        LocalDate onDay = LocalDate.of(2026, 11, 23);
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(49999L, coupon, onDay));

        assertEquals(5000L, pricing.calculateDiscount(50000L, coupon, onDay));
    }
}
