package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void couponConstructorFixedValueGreaterThan100Allowed() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        Coupon coupon = new Coupon("CODE", CouponType.FIXED, 500000L, 0L, 0L, expiry);
        assertEquals(500000L, coupon.getValue());
    }
}
