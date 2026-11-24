package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void couponRejectsEachInvalidFieldForTheApplicableTypes() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        for (String blank : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class,
                () -> new Coupon(blank, CouponType.FIXED, 1, 0, 0, expiry));
        }
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 1, 0, 0, expiry));
        assertThrows(IllegalArgumentException.class,
            () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, expiry));
        for (CouponType type : CouponType.values()) {
            for (long invalid : new long[]{0, -1}) {
                assertThrows(IllegalArgumentException.class,
                    () -> new Coupon("C", type, invalid, 0, 0, expiry));
            }
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, -1, 0, expiry));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, -1, expiry));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, 0, null));
        }
    }
}
