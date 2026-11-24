package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void couponConstructorInvalidArguments() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);

        assertThrows(IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("", CouponType.PERCENT, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("   ", CouponType.PERCENT, 10L, 0L, 0L, expiry));

        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", null, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 0L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, -1L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.FIXED, 0L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 101L, 0L, 0L, expiry));

        assertDoesNotThrow(() -> new Coupon("C1", CouponType.FIXED, 101L, 0L, 0L, expiry));
        assertDoesNotThrow(() -> new Coupon("C1", CouponType.PERCENT, 1L, 0L, 0L, expiry));

        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 10L, -1L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 10L, 0L, -1L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.FIXED, 10L, 0L, -1L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 10L, 0L, 0L, null));
    }
}
