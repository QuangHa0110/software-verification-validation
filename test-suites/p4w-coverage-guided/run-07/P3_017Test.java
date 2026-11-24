package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void couponConstructorValidFixedInitializesProperly() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        Coupon coupon = new Coupon("FIXED50K", CouponType.FIXED, 50000L, 0L, 0L, expiry);
        assertEquals("FIXED50K", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(50000L, coupon.getValue());
        assertEquals(0L, coupon.getMinOrder());
        assertEquals(0L, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
    }
}
