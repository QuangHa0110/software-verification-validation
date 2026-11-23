package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void testCouponConstructorInvalidTerms() {
        LocalDate date = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 100L, -1L, 0L, date));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 100L, 0L, -1L, date));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.FIXED, 100L, 0L, 0L, null));
    }
}
