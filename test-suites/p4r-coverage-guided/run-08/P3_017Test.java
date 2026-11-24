package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @ParameterizedTest
    @CsvSource({"0,1,0", "399,1,399", "400,1,400", "401,1,400", "401,0,400"})
    void fixedDiscountIgnoresCapAndCannotExceedSubtotal(long subtotal, long cap, long expected) {
        LocalDate day = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 400, 0, cap, day);
        assertEquals(expected, new PricingService().calculateDiscount(subtotal, coupon, day));
    }
}
