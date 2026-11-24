package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_061Test {

    @ParameterizedTest
    @CsvSource({
        "99999, 10000, 10000",
        "99999, 0, 14999",
        "99999, 14999, 14999",
        "99999, 15000, 14999",
        "100000, 10000, 10000"
    })
    void pricingServicePercentDiscounts(long subtotal, long maxDiscount, long expectedDiscount) {
        PricingService ps = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        long percent = (subtotal == 100000L) ? 10L : 15L;
        Coupon coupon = new Coupon("C1", CouponType.PERCENT, percent, 0L, maxDiscount, expiry);

        assertEquals(expectedDiscount, ps.calculateDiscount(subtotal, coupon, expiry));
    }
}
