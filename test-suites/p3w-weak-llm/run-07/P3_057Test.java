package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_057Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithCapExceeded() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("PERC15", CouponType.PERCENT, 15L, 50000L, 10000L, today);
        long discount = pricingService.calculateDiscount(99999L, coupon, today);
        assertEquals(10000L, discount);
    }
}
