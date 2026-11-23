package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void testCouponConstructorValid() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        Coupon c1 = new Coupon("DISC10", CouponType.PERCENT, 10L, 100000L, 50000L, expiry);
        assertEquals("DISC10", c1.getCode());
        assertEquals(CouponType.PERCENT, c1.getType());
        assertEquals(10L, c1.getValue());
        assertEquals(100000L, c1.getMinOrder());
        assertEquals(50000L, c1.getMaxDiscount());
        assertEquals(expiry, c1.getExpiry());

        Coupon c2 = new Coupon("FIXED50", CouponType.FIXED, 50000L, 0L, 0L, expiry);
        assertEquals(CouponType.FIXED, c2.getType());
        assertEquals(50000L, c2.getValue());
        assertEquals(0L, c2.getMinOrder());
        assertEquals(0L, c2.getMaxDiscount());
    }
}
