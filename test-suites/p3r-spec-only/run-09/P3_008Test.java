package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @ParameterizedTest
    @CsvSource({"PERCENT,1,0,0", "PERCENT,100,17,9", "FIXED,101,0,0", "FIXED,150,30,1"})
    void couponStoresAllSuppliedFields(CouponType type, long value, long minimum, long cap) {
        LocalDate expiry = LocalDate.of(2000, 2, 29);
        Coupon coupon = new Coupon(" C ", type, value, minimum, cap, expiry);
        assertEquals(" C ", coupon.getCode());
        assertEquals(type, coupon.getType());
        assertEquals(value, coupon.getValue());
        assertEquals(minimum, coupon.getMinOrder());
        assertEquals(cap, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
    }
}
