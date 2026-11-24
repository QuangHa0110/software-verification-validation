package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_056Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithoutMaxCap() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 25);
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P10", CouponType.PERCENT, 10L, 0L, 0L, expiry);
        long discount = pricing.calculateDiscount(200000L, coupon, today);
        assertEquals(20000L, discount);
    }
}
