package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void couponConstructorInvalidArguments() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("", CouponType.PERCENT, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("   ", CouponType.PERCENT, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 10L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 0L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, -1L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 101L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 0L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 50L, -1L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 50L, 0L, -1L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 50L, 0L, 0L, null));
    }
}
