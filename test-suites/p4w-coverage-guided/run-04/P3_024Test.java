package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void pricingServiceCalculateDiscountValidation() {
        PricingService ps = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 100L, 0L, 0L, today);

        assertThrows(IllegalArgumentException.class, () -> ps.calculateDiscount(-1L, coupon, today));
        assertThrows(IllegalArgumentException.class, () -> ps.calculateDiscount(-1L, null, null));
        assertThrows(IllegalArgumentException.class, () -> ps.calculateDiscount(100L, coupon, null));
        assertEquals(0L, ps.calculateDiscount(100L, null, today));
    }
}
