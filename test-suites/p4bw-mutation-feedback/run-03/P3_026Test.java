package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void couponValidPercentCreationAndGetters() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon c = new Coupon("C1", CouponType.PERCENT, 100L, 0L, 0L, expiry);
        assertEquals("C1", c.getCode());
        assertEquals(CouponType.PERCENT, c.getType());
        assertEquals(100L, c.getValue());
        assertEquals(0L, c.getMinOrder());
        assertEquals(0L, c.getMaxDiscount());
        assertEquals(expiry, c.getExpiry());
    }
}
