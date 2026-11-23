package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void couponValidFixedCreationAndGetters() {
        LocalDate expiry = LocalDate.of(2026, 11, 24);
        Coupon c = new Coupon("C2", CouponType.FIXED, 150L, 30L, 1L, expiry);
        assertEquals("C2", c.getCode());
        assertEquals(CouponType.FIXED, c.getType());
        assertEquals(150L, c.getValue());
        assertEquals(30L, c.getMinOrder());
        assertEquals(1L, c.getMaxDiscount());
        assertEquals(expiry, c.getExpiry());
    }
}
