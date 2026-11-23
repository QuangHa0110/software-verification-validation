package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_059Test {

    @Test
    void pricingServiceCalculateDiscountFixedCapsAtSubtotal() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("FIXED400", CouponType.FIXED, 400L, 0L, 100L, today);
        long discount = pricingService.calculateDiscount(300L, coupon, today);
        assertEquals(300L, discount);
    }
}
