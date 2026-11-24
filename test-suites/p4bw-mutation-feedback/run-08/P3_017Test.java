package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void couponConstructorInvalidTypeOrValue() {
        LocalDate d = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 10L, 0L, 0L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 0L, 0L, 0L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, -5L, 0L, 0L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 101L, 0L, 0L, d));

        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 0L, 0L, 0L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, -10L, 0L, 0L, d));
        assertDoesNotThrow(() -> new Coupon("C", CouponType.FIXED, 101L, 0L, 0L, d));
    }
}
