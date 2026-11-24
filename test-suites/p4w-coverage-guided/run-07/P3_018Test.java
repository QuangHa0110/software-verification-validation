package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void couponConstructorInvalidCodeThrowsException(String invalidCode) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon(invalidCode, CouponType.PERCENT, 10L, 0L, 0L, expiry));
    }
}
