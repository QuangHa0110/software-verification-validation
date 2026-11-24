package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void couponConstructorValidatesCodeTypeAmountsAndExpiryIndependently() {
        LocalDate date = LocalDate.of(2000, 1, 1);
        for (String blank : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Coupon(blank, CouponType.FIXED, 1, 0, 0, date));
        }
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 1, 0, 0, date));
        for (CouponType type : CouponType.values()) {
            for (long value : new long[]{-1, 0}) {
                assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, value, 0, 0, date));
            }
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, -1, 0, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, -1, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, 0, null));
        }
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, date));
    }
}
