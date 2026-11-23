package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testCouponConstructorInvalidCode(String invalidCode) {
        LocalDate date = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon(invalidCode, CouponType.PERCENT, 10L, 0L, 0L, date));
        assertThrows(IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 10L, 0L, 0L, date));
    }
}
