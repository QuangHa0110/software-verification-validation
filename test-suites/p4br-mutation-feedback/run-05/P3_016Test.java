package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @ParameterizedTest
    @CsvSource({"0,0", "300,300", "400,400", "401,400", "3000000000,400"})
    void fixedDiscountIsLimitedOnlyBySubtotal(long subtotal, long expected) {
        LocalDate end = LocalDate.of(2026, 11, 23);
        PricingService pricing = new PricingService();
        for (long cap : new long[] {0, 1, 401}) {
            Coupon coupon = new Coupon("F", CouponType.FIXED, 400, 0, cap, end);
            assertEquals(expected, pricing.calculateDiscount(subtotal, coupon, end));
        }
    }
}
