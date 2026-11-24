package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void couponConstructorInvalidTerms() {
        LocalDate d = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 10L, -1L, 0L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 10L, 0L, -1L, d));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 10L, 0L, 0L, null));
    }
}
