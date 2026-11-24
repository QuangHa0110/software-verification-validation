package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_058Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithCapNotExceeded() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("PERC10", CouponType.PERCENT, 10L, 0L, 20000L, today);
        long discount = pricingService.calculateDiscount(100000L, coupon, today);
        assertEquals(10000L, discount);
    }
}
