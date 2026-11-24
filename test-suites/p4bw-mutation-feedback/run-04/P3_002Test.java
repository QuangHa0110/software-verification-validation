package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void enumValuesAndValueOfCouponType() {
        CouponType[] values = CouponType.values();
        assertEquals(2, values.length);
        assertSame(CouponType.PERCENT, values[0]);
        assertSame(CouponType.FIXED, values[1]);
        assertSame(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertSame(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("percent"));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
    }
}
