package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void pricingServiceCalculateDiscountValidationOrder() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 100L, 50L, 0L, today);

        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100L, coupon, null));

        assertEquals(0L, pricing.calculateDiscount(100L, null, today));
    }
}
