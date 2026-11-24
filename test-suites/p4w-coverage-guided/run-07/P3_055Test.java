package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_055Test {

    @Test
    void pricingServiceCalculateDiscountSubtotalBelowMinOrderThrowsException() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("MIN", CouponType.PERCENT, 10L, 200000L, 0L, today);
        assertThrows(CouponNotApplicableException.class, () -> pricingService.calculateDiscount(150000L, coupon, today));
    }
}
