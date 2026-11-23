package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_062Test {

    @Test
    void pricingServicePercentDivisionRoundsDown() {
        PricingService ps = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C1", CouponType.PERCENT, 1L, 0L, 0L, expiry);

        assertEquals(0L, ps.calculateDiscount(7L, coupon, expiry));
    }
}
