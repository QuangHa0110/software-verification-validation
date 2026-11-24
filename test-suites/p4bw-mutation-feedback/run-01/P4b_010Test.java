package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_010Test {

    @Test
    void killPricingServiceCalculateDiscountSubtotalEqualsMinOrderBoundary() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("MIN100K", CouponType.FIXED, 10_000L, 100_000L, 0L, today);

        // subtotal == minOrder should be applicable
        long discount = pricingService.calculateDiscount(100_000L, coupon, today);
        assertEquals(10_000L, discount);
    }
}
