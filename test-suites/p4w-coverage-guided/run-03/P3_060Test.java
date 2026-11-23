package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_060Test {

    @Test
    void pricingServiceCouponNotApplicableThrowsWhenBelowMinOrder() {
        PricingService ps = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C1", CouponType.PERCENT, 15L, 99999L, 10000L, expiry);

        assertThrows(CouponNotApplicableException.class, () -> ps.calculateDiscount(99998L, coupon, expiry));
    }
}
