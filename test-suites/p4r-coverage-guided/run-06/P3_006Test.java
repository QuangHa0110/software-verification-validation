package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void couponValidatesEachFieldAndLimitsOnlyPercentValuesToOneHundred() {
        LocalDate expiry = LocalDate.of(2000, 1, 1);
        for (String blank : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class,
                () -> new Coupon(blank, CouponType.FIXED, 1, 0, 0, expiry));
        }
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 1, 0, 0, expiry));
        for (CouponType type : CouponType.values()) {
            for (int value : new int[] {-1, 0}) {
                assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, value, 0, 0, expiry));
            }
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, -1, 0, expiry));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, -1, expiry));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, 0, null));
        }
        assertThrows(IllegalArgumentException.class,
            () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, expiry));

        for (long value : new long[] {1, 100}) {
            Coupon percent = new Coupon("P", CouponType.PERCENT, value, 0, 0, expiry);
            assertEquals(value, percent.getValue());
            assertEquals(CouponType.PERCENT, percent.getType());
        }
        Coupon fixed = new Coupon(" F ", CouponType.FIXED, 101, 30, 2, expiry);
        assertEquals(" F ", fixed.getCode());
        assertEquals(CouponType.FIXED, fixed.getType());
        assertEquals(101L, fixed.getValue());
        assertEquals(30L, fixed.getMinOrder());
        assertEquals(2L, fixed.getMaxDiscount());
        assertEquals(expiry, fixed.getExpiry());
    }
}
