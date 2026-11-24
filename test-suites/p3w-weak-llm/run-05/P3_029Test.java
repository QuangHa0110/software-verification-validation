package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void testPricingServiceCalculateDiscountMinOrderNotMet() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("DISC10", CouponType.PERCENT, 10L, 200000L, 0L, today);
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(199999L, coupon, today));
        assertDoesNotThrow(() -> pricing.calculateDiscount(200000L, coupon, today));
    }
}
