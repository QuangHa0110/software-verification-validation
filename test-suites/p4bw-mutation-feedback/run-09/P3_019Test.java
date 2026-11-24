package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void pricingServiceCalculateDiscountFixedEdgeCases() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon c1 = new Coupon("C1", CouponType.FIXED, 400L, 0L, 1L, today);
        assertEquals(300L, pricingService.calculateDiscount(300L, c1, today));
        assertEquals(0L, pricingService.calculateDiscount(0L, c1, today));
        assertEquals(400L, pricingService.calculateDiscount(500L, c1, today));
        assertEquals(400L, pricingService.calculateDiscount(400L, c1, today));
    }
}
