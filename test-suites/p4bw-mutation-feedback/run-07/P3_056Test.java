package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_056Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithoutCap() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("PERC15", CouponType.PERCENT, 15L, 50000L, 0L, today);
        long discount = pricingService.calculateDiscount(99999L, coupon, today);
        assertEquals(14999L, discount);
    }
}
