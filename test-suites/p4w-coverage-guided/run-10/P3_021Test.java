package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void testCouponConstructorValidAndGetters() {
        LocalDate d1 = LocalDate.of(2026, 11, 23);
        Coupon cA = new Coupon("C1", CouponType.PERCENT, 100L, 0L, 0L, d1);
        assertEquals("C1", cA.getCode());
        assertEquals(CouponType.PERCENT, cA.getType());
        assertEquals(100L, cA.getValue());
        assertEquals(0L, cA.getMinOrder());
        assertEquals(0L, cA.getMaxDiscount());
        assertEquals(d1, cA.getExpiry());

        LocalDate d2 = LocalDate.of(2026, 11, 24);
        Coupon cB = new Coupon("C2", CouponType.FIXED, 150L, 30L, 1L, d2);
        assertEquals("C2", cB.getCode());
        assertEquals(CouponType.FIXED, cB.getType());
        assertEquals(150L, cB.getValue());
        assertEquals(30L, cB.getMinOrder());
        assertEquals(1L, cB.getMaxDiscount());
        assertEquals(d2, cB.getExpiry());
    }
}
