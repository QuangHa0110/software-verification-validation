package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void pricingServiceFixedCalculations() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);

        Coupon cFixed = new Coupon("C", CouponType.FIXED, 400L, 0L, 1L, today);
        assertEquals(300L, pricing.calculateDiscount(300L, cFixed, today));
        assertEquals(0L, pricing.calculateDiscount(0L, cFixed, today));
        assertEquals(400L, pricing.calculateDiscount(500L, cFixed, today));
    }
}
