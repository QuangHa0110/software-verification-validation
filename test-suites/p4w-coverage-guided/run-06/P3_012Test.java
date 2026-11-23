package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void couponConstructorValidAndGetters() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon c1 = new Coupon("PERCENT10", CouponType.PERCENT, 10L, 50000L, 20000L, expiry);
        assertEquals("PERCENT10", c1.getCode());
        assertEquals(CouponType.PERCENT, c1.getType());
        assertEquals(10L, c1.getValue());
        assertEquals(50000L, c1.getMinOrder());
        assertEquals(20000L, c1.getMaxDiscount());
        assertEquals(expiry, c1.getExpiry());

        Coupon c2 = new Coupon("FIXED150", CouponType.FIXED, 150000L, 0L, 0L, expiry);
        assertEquals(150000L, c2.getValue());
        assertEquals(0L, c2.getMinOrder());
        assertEquals(0L, c2.getMaxDiscount());

        Coupon c3 = new Coupon("PERCENT100", CouponType.PERCENT, 100L, 0L, 0L, expiry);
        assertEquals(100L, c3.getValue());
    }
}
