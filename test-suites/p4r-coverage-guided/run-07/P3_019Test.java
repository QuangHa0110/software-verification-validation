package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @ParameterizedTest
    @CsvSource({"0,0", "299,299", "300,300", "301,300"})
    void fixedDiscountIsLimitedBySubtotalAndIgnoresCouponCap(long subtotal, long expected) {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        PricingService pricing = new PricingService();
        for (long cap : new long[] {0, 1, 500}) {
            Coupon coupon = new Coupon("F", CouponType.FIXED, 300, 0, cap, expiry);
            assertEquals(expected, pricing.calculateDiscount(subtotal, coupon, expiry));
        }
    }
}
