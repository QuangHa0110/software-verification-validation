package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_060Test {

    @Test
    void pricingServiceCalculateDiscountFixedLessThanSubtotal() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("FIXED20K", CouponType.FIXED, 20000L, 0L, 0L, today);
        long discount = pricingService.calculateDiscount(50000L, coupon, today);
        assertEquals(20000L, discount);
    }
}
