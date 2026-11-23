package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @ValueSource(longs = {-1L, -100L})
    void couponConstructorInvalidMaxDiscount(long maxDiscount) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 10L, 0L, maxDiscount, expiry));
    }
}
