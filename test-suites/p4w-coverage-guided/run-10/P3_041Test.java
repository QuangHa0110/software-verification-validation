package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @ParameterizedTest
    @CsvSource({
        "300, 400, 0, 1, 300",
        "0, 400, 0, 1, 0",
        "500, 200, 0, 0, 200"
    })
    void testPricingServiceFixedDiscounts(long subtotal, long value, long minOrder, long maxDiscount, long expectedDiscount) {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.FIXED, value, minOrder, maxDiscount, today);

        assertEquals(expectedDiscount, pricing.calculateDiscount(subtotal, coupon, today));
    }
}
