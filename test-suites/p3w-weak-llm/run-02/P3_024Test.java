package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void pricingServiceMinOrderCheck() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, 10L, 100000L, 0L, today);

        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99999L, coupon, today));
        assertEquals(10000L, pricing.calculateDiscount(100000L, coupon, today));
        assertEquals(15000L, pricing.calculateDiscount(150000L, coupon, today));
    }
}
