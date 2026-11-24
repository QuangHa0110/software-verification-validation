package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,7,12", "FIXED,101,30,1", "FIXED,500,0,0"})
    void couponStoresAllTermsIncludingOriginalCodeAndPastExpiry(
            CouponType type, long value, long minimum, long cap) {
        LocalDate expiry = LocalDate.of(2001, 2, 3);
        Coupon coupon = new Coupon(" C1\t", type, value, minimum, cap, expiry);
        assertEquals(" C1\t", coupon.getCode());
        assertEquals(type, coupon.getType());
        assertEquals(value, coupon.getValue());
        assertEquals(minimum, coupon.getMinOrder());
        assertEquals(cap, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
    }
}
