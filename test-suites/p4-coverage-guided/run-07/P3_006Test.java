package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,17,23", "FIXED,101,30,1", "FIXED,150,0,0"})
    void couponStoresOriginalTermsIncludingPastExpiry(CouponType type, long value, long minimum, long cap) {
        LocalDate expiry = LocalDate.of(1999, 1, 2);
        Coupon coupon = new Coupon(" Code ", type, value, minimum, cap, expiry);
        assertEquals(" Code ", coupon.getCode());
        assertEquals(type, coupon.getType());
        assertEquals(value, coupon.getValue());
        assertEquals(minimum, coupon.getMinOrder());
        assertEquals(cap, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
    }
}
