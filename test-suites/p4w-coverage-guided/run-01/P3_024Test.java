package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -50L})
    void couponConstructorInvalidValue(long value) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, value, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.FIXED, value, 0L, 0L, expiry));
    }
}
