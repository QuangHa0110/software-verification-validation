package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void couponValidatesEveryTermAndPreservesAcceptedValues() {
        LocalDate expiry = LocalDate.of(2001, 2, 3);
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
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, expiry));
        for (int value : new int[] {1, 100}) {
            assertEquals(value, new Coupon("C", CouponType.PERCENT, value, 0, 0, expiry).getValue());
        }
        Coupon fixed = new Coupon(" C ", CouponType.FIXED, 101, 17, 3, expiry);
        assertEquals(" C ", fixed.getCode());
        assertEquals(CouponType.FIXED, fixed.getType());
        assertEquals(101L, fixed.getValue());
        assertEquals(17L, fixed.getMinOrder());
        assertEquals(3L, fixed.getMaxDiscount());
        assertEquals(expiry, fixed.getExpiry());
    }
}
