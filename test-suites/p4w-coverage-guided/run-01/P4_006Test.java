package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4_006Test {

    @Test
    void pricingServiceCalculateDiscountPercentWithPositiveCapBelowOrEqualCap() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P10_CAP", CouponType.PERCENT, 10, 0, 10_000, today);

        long discount = pricingService.calculateDiscount(100_000, coupon, today);

        assertEquals(10_000, discount);
    }
}
