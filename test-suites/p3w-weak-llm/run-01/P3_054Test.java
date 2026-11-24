package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_054Test {

    @Test
    void pricingServiceCalculateDiscountSubtotalBelowMinOrderThrows() {
        PricingService pricing = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 25);
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("MIN", CouponType.FIXED, 10000L, 50000L, 0L, expiry);
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(49999L, coupon, today));
    }
}
