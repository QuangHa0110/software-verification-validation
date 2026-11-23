package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_063Test {

    @ParameterizedTest
    @CsvSource({
        "300, 400, 300",
        "0, 400, 0",
        "500, 400, 400",
        "400, 400, 400"
    })
    void pricingServiceFixedDiscountsIgnoresMaxDiscount(long subtotal, long value, long expectedDiscount) {
        PricingService ps = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C1", CouponType.FIXED, value, 0L, 1L, expiry);

        assertEquals(expectedDiscount, ps.calculateDiscount(subtotal, coupon, expiry));
    }
}
