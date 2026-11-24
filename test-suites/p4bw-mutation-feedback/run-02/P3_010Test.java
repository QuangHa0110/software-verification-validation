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
        LocalDate d1 = LocalDate.of(2026, 11, 23);
        Coupon c1 = new Coupon("C1", CouponType.PERCENT, 100L, 0L, 0L, d1);
        assertEquals("C1", c1.getCode());
        assertEquals(CouponType.PERCENT, c1.getType());
        assertEquals(100L, c1.getValue());
        assertEquals(0L, c1.getMinOrder());
        assertEquals(0L, c1.getMaxDiscount());
        assertEquals(d1, c1.getExpiry());

        LocalDate d2 = LocalDate.of(2026, 11, 24);
        Coupon c2 = new Coupon(" C2 ", CouponType.FIXED, 150L, 30L, 1L, d2);
        assertEquals(" C2 ", c2.getCode());
        assertEquals(CouponType.FIXED, c2.getType());
        assertEquals(150L, c2.getValue());
        assertEquals(30L, c2.getMinOrder());
        assertEquals(1L, c2.getMaxDiscount());
        assertEquals(d2, c2.getExpiry());
    }
}
