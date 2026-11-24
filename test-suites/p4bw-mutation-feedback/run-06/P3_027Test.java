package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void pricingServiceCalculateDiscountValidationAndNullCoupon() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, null, today));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(10000L, null, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, null, null));

        Coupon coupon = new Coupon("C", CouponType.FIXED, 1000L, 0L, 0L, today);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(10000L, coupon, null));

        assertEquals(0L, pricing.calculateDiscount(0L, null, today));
        assertEquals(0L, pricing.calculateDiscount(100000L, null, today));
    }
}
