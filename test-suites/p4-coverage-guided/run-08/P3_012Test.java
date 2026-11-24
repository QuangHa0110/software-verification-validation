package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @ParameterizedTest
    @CsvSource({
        "PERCENT,99999,15,99999,0,14999",
        "PERCENT,99999,15,99999,14999,14999",
        "PERCENT,99999,15,99999,10000,10000",
        "PERCENT,99999,15,99999,15000,14999",
        "PERCENT,7,1,0,0,0",
        "PERCENT,3000000001,100,0,0,3000000001",
        "FIXED,0,400,0,1,0",
        "FIXED,300,400,0,1,300",
        "FIXED,400,400,0,1,400",
        "FIXED,401,400,0,1,400"
    })
    void discountUsesWholeVndAndTheCapAppropriateToItsType(
            CouponType type, long subtotal, long value, long minimum, long cap, long expected) {
        LocalDate date = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("C", type, value, minimum, cap, date);
        assertEquals(expected, new PricingService().calculateDiscount(subtotal, coupon, date));
    }
}
