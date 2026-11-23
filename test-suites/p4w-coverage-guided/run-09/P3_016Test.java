package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void pricingServiceCalculateDiscountValidation() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C1", CouponType.PERCENT, 10L, 0L, 0L, today);

        assertThrows(IllegalArgumentException.class, () -> pricingService.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> pricingService.calculateDiscount(100L, coupon, null));
        assertThrows(IllegalArgumentException.class, () -> pricingService.calculateDiscount(-1L, null, null));

        assertEquals(0L, pricingService.calculateDiscount(100L, null, today));
        assertEquals(0L, pricingService.calculateDiscount(0L, null, today));
    }
}
