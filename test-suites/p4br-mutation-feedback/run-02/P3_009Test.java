package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,7,4", "FIXED,101,30,1", "FIXED,101,0,0"})
    void couponKeepsOriginalFieldsIncludingPastExpiry(CouponType type, long value, long minimum, long cap) {
        LocalDate date = LocalDate.of(1999, 12, 31);
        Coupon coupon = new Coupon(" Code ", type, value, minimum, cap, date);
        assertEquals(" Code ", coupon.getCode());
        assertEquals(type, coupon.getType());
        assertEquals(value, coupon.getValue());
        assertEquals(minimum, coupon.getMinOrder());
        assertEquals(cap, coupon.getMaxDiscount());
        assertEquals(date, coupon.getExpiry());
    }
}
