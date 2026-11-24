package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_057Test {

    @Test
    void pricingServiceCalculateDiscountFixedCapsAtSubtotal() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 25);
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("FIXED", CouponType.FIXED, 50000L, 0L, 0L, expiry);
        long discount = pricing.calculateDiscount(30000L, coupon, today);
        assertEquals(30000L, discount);
    }
}
