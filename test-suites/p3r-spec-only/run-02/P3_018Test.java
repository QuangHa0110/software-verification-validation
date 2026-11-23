package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @ParameterizedTest
    @CsvSource({
        "99999,15,99999,0,14999",
        "99999,15,99999,14999,14999",
        "99999,15,99999,10000,10000",
        "99999,15,99999,15000,14999",
        "7,1,0,0,0",
        "0,100,0,0,0",
        "3000000000,100,0,0,3000000000"
    })
    void percentDiscountFloorsInLongArithmeticAndHonorsPositiveCap(
            long subtotal, long percent, long minimum, long cap, long expected) {
        LocalDate date = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P", CouponType.PERCENT, percent, minimum, cap, date);
        assertEquals(expected, new PricingService().calculateDiscount(subtotal, coupon, date));
    }
}
