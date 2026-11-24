package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @Test
    void couponRejectsMissingFieldsAndInvalidAmountsForEachType() {
        LocalDate date = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", null, 1, 0, 0, date));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C", CouponType.PERCENT, 101, 0, 0, date));
        for (CouponType type : CouponType.values()) {
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 0, 0, 0, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, -1, 0, 0, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, -1, 0, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, -1, date));
            assertThrows(IllegalArgumentException.class, () -> new Coupon("C", type, 1, 0, 0, null));
        }
    }
}
