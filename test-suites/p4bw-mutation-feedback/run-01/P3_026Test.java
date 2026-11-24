package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @ParameterizedTest
    @ValueSource(longs = {-1L, -100L})
    void couponConstructorInvalidMinOrder(long minOrder) {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.FIXED, 50L, minOrder, 0L, expiry));
    }
}
