package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_055Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithMaxCap() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 25);
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P10", CouponType.PERCENT, 10L, 0L, 15000L, expiry);
        long discount = pricing.calculateDiscount(200000L, coupon, today);
        assertEquals(15000L, discount);
    }
}
