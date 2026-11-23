package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void pricingServiceCalculateDiscountMinOrderNotMet() {
        PricingService pricingService = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("CODE", CouponType.PERCENT, 10L, 100000L, 0L, expiry);

        LocalDate today = LocalDate.of(2026, 11, 23);
        assertThrows(CouponNotApplicableException.class, () -> pricingService.calculateDiscount(99999L, coupon, today));
        assertEquals(10000L, pricingService.calculateDiscount(100000L, coupon, today));
    }
}
