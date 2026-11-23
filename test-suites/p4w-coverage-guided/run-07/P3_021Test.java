package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void couponConstructorNonPositiveValueThrowsException(long invalidVal) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("CODE", CouponType.PERCENT, invalidVal, 0L, 0L, expiry));
    }
}
