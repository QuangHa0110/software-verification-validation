package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void couponValidatesTypeValueMinimumCapAndExpiry() {
        LocalDate date = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 1, 0, 0, date));
        for (CouponType type : CouponType.values()) {
            for (long invalid : new long[] {-1, 0}) {
                assertThrows(IllegalArgumentException.class,
                    () -> new Coupon("C", type, invalid, 0, 0, date));
            }
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, -1, 0, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, -1, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, 0, null));
        }
        assertThrows(IllegalArgumentException.class,
            () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, date));
    }
}
