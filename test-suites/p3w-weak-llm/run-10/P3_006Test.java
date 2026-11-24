package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void testCouponTypeValueOfInvalidThrowsException() {
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("percent"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("UNKNOWN"));
    }
}
