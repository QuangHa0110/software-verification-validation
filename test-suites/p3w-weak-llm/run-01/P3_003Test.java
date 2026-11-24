package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void couponTypeEnumValuesAndValueOf() {
        CouponType[] values = CouponType.values();
        assertEquals(2, values.length);
        assertEquals(CouponType.PERCENT, values[0]);
        assertEquals(CouponType.FIXED, values[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("percent"));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
    }
}
