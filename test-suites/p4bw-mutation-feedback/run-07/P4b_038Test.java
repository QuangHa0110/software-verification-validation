package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_038Test {

    @Test
    void pricingServiceCalculateDiscountSubtotalZeroReturnsZero() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 24);
        Coupon coupon = new Coupon("FIXED10", CouponType.FIXED, 10_000L, 0L, 0L, today.plusDays(1));

        long discount = pricingService.calculateDiscount(0L, coupon, today);
        assertEquals(0L, discount);
    }
}
