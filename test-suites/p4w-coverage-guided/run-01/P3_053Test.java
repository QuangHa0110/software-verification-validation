package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_053Test {

    @Test
    void pricingServiceCalculateDiscountExpiredCouponThrows() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 20);
        LocalDate today = LocalDate.of(2026, 11, 21);
        Coupon coupon = new Coupon("EXP", CouponType.FIXED, 10000L, 0L, 0L, expiry);
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(100000L, coupon, today));
    }
}
