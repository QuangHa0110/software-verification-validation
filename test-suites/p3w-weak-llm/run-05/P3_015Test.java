package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -10L})
    void testCouponConstructorInvalidValue(long val) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("CODE", CouponType.PERCENT, val, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("CODE", CouponType.FIXED, val, 0L, 0L, expiry));
    }
}
