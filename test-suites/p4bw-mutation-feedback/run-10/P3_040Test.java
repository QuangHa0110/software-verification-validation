package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_040Test {

    @ParameterizedTest
    @CsvSource({
        "99999, 15, 99999, 10000, 10000",
        "99999, 15, 99999, 0, 14999",
        "100000, 10, 0, 10000, 10000",
        "7, 1, 0, 0, 0"
    })
    void testPricingServicePercentDiscounts(long subtotal, long percent, long minOrder, long maxDiscount, long expectedDiscount) {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", CouponType.PERCENT, percent, minOrder, maxDiscount, today);

        assertEquals(expectedDiscount, pricing.calculateDiscount(subtotal, coupon, today));
    }
}
