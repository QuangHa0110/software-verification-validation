package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,31,17", "FIXED,101,43,2", "FIXED,101,0,0"})
    void couponStoresOriginalCodeAndAllSuppliedValues(CouponType type, long value,
                                                      long minimum, long cap) {
        LocalDate end = LocalDate.of(2000, 1, 1);
        Coupon coupon = new Coupon(" Code ", type, value, minimum, cap, end);
        assertEquals(" Code ", coupon.getCode());
        assertEquals(type, coupon.getType());
        assertEquals(value, coupon.getValue());
        assertEquals(minimum, coupon.getMinOrder());
        assertEquals(cap, coupon.getMaxDiscount());
        assertEquals(end, coupon.getExpiry());
    }
}
