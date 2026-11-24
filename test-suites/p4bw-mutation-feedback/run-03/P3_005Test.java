package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void couponTypeEnumValuesAndOrder() {
        CouponType[] values = CouponType.values();
        assertEquals(2, values.length);
        assertEquals(CouponType.PERCENT, values[0]);
        assertEquals(CouponType.FIXED, values[1]);
    }
}
