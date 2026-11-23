package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void testCouponConstructorPercentLimit() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertDoesNotThrow(() -> new Coupon("C100", CouponType.PERCENT, 100L, 0L, 0L, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C101", CouponType.PERCENT, 101L, 0L, 0L, expiry));
        assertDoesNotThrow(() -> new Coupon("C1000", CouponType.FIXED, 100000L, 0L, 0L, expiry));
    }
}
