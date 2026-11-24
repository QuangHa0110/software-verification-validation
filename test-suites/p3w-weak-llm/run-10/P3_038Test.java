package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void testPricingServiceInvalidInputs() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 10L, 0L, 0L, today);

        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100L, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1L, null, null));
    }
}
