package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @Test
    void pricingServiceCalculateDiscountExpiredCoupon() {
        PricingService pricingService = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("CODE", CouponType.PERCENT, 10L, 50000L, 0L, expiry);

        LocalDate nextDay = LocalDate.of(2026, 11, 24);
        assertThrows(CouponExpiredException.class, () -> pricingService.calculateDiscount(100000L, coupon, nextDay));
        assertThrows(CouponExpiredException.class, () -> pricingService.calculateDiscount(10000L, coupon, nextDay));

        LocalDate sameDay = LocalDate.of(2026, 11, 23);
        assertDoesNotThrow(() -> pricingService.calculateDiscount(100000L, coupon, sameDay));
    }
}
