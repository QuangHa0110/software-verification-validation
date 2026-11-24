package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void couponConstructorFixedSuccess() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        Coupon coupon = new Coupon("FIXED200K", CouponType.FIXED, 200000L, 0L, 0L, expiry);
        assertEquals("FIXED200K", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(200000L, coupon.getValue());
    }
}
