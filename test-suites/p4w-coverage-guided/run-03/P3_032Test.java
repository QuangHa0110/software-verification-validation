package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void couponFixedCanExceedHundred() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon c = new Coupon("C1", CouponType.FIXED, 101L, 0L, 0L, expiry);
        assertEquals(101L, c.getValue());
    }
}
