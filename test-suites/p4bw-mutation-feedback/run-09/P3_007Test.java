package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @Test
    void couponConstructorValidAndGetters() {
        Coupon c1 = new Coupon("C1", CouponType.PERCENT, 100L, 0L, 0L, LocalDate.of(2026, 11, 23));
        assertEquals("C1", c1.getCode());
        assertEquals(CouponType.PERCENT, c1.getType());
        assertEquals(100L, c1.getValue());
        assertEquals(0L, c1.getMinOrder());
        assertEquals(0L, c1.getMaxDiscount());
        assertEquals(LocalDate.of(2026, 11, 23), c1.getExpiry());

        Coupon c2 = new Coupon("C2", CouponType.FIXED, 150L, 30L, 1L, LocalDate.of(2026, 11, 24));
        assertEquals("C2", c2.getCode());
        assertEquals(CouponType.FIXED, c2.getType());
        assertEquals(150L, c2.getValue());
        assertEquals(30L, c2.getMinOrder());
        assertEquals(1L, c2.getMaxDiscount());
        assertEquals(LocalDate.of(2026, 11, 24), c2.getExpiry());
    }
}
