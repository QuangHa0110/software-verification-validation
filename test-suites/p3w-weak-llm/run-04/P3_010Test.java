package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void couponConstructorValidAndGetters() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon c1 = new Coupon("C1", CouponType.PERCENT, 100L, 0L, 0L, expiry);
        assertEquals("C1", c1.getCode());
        assertSame(CouponType.PERCENT, c1.getType());
        assertEquals(100L, c1.getValue());
        assertEquals(0L, c1.getMinOrder());
        assertEquals(0L, c1.getMaxDiscount());
        assertEquals(expiry, c1.getExpiry());

        Coupon c2 = new Coupon(" C2 ", CouponType.FIXED, 150000L, 50000L, 20000L, expiry);
        assertEquals(" C2 ", c2.getCode());
        assertSame(CouponType.FIXED, c2.getType());
        assertEquals(150000L, c2.getValue());
        assertEquals(50000L, c2.getMinOrder());
        assertEquals(20000L, c2.getMaxDiscount());
        assertEquals(expiry, c2.getExpiry());
    }
}
