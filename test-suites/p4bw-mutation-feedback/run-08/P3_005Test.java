package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void enumCouponTypeValueOf() {
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("percent"));
    }
}
