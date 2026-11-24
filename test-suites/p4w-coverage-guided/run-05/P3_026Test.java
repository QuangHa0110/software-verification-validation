package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void testPricingServiceCalculateDiscountInvalidArguments() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("DISC10", CouponType.PERCENT, 10L, 100000L, 50000L, today);

        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100000L, coupon, null));
    }
}
