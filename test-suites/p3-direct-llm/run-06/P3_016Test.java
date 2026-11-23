package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @ParameterizedTest
    @CsvSource({
        "99999,15,0,14999", "99999,15,14999,14999", "99999,15,10000,10000",
        "99999,15,15000,14999", "7,1,0,0", "0,100,0,0",
        "3000000001,100,0,3000000001", "3000000001,80,0,2400000000"
    })
    void percentageDiscountUsesLongArithmeticFloorAndOptionalCap(
            long subtotal, long percent, long cap, long expected) {
        LocalDate day = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon("P", CouponType.PERCENT, percent, subtotal, cap, day);
        PricingService pricing = new PricingService();
        assertEquals(expected, pricing.calculateDiscount(subtotal, coupon, day));
        assertEquals(expected, pricing.calculateDiscount(subtotal, coupon, day));
    }
}
