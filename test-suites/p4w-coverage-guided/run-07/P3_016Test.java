package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void couponConstructorValidPercentInitializesProperly() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        Coupon coupon = new Coupon("DISC10", CouponType.PERCENT, 10L, 50000L, 20000L, expiry);
        assertEquals("DISC10", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(10L, coupon.getValue());
        assertEquals(50000L, coupon.getMinOrder());
        assertEquals(20000L, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
    }
}
