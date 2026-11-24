package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void pricingServiceCalculateDiscountExpiryAndMinOrder() {
        PricingService pricingService = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C1", CouponType.PERCENT, 15L, 99999L, 10000L, expiry);

        assertThrows(CouponExpiredException.class, () -> pricingService.calculateDiscount(99998L, coupon, LocalDate.of(2026, 11, 24)));
        assertThrows(CouponExpiredException.class, () -> pricingService.calculateDiscount(100000L, coupon, LocalDate.of(2026, 11, 24)));

        assertThrows(CouponNotApplicableException.class, () -> pricingService.calculateDiscount(99998L, coupon, expiry));

        long discountOnMin = pricingService.calculateDiscount(99999L, coupon, expiry);
        assertEquals(10000L, discountOnMin);
    }
}
