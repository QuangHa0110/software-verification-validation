package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void pricingServiceCouponExpiryCheck() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 100L, 50000L, 0L, expiry);

        LocalDate nextDay = LocalDate.of(2026, 11, 24);
        assertThrows(CouponExpiredException.class, () -> pricing.calculateDiscount(10000L, coupon, nextDay));

        assertEquals(100L, pricing.calculateDiscount(60000L, coupon, expiry));
        assertEquals(100L, pricing.calculateDiscount(60000L, coupon, LocalDate.of(2026, 11, 22)));
    }
}
