package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @ParameterizedTest
    @CsvSource({
        "PERCENT, PERCENT",
        "FIXED, FIXED"
    })
    void testCouponTypeValueOfValid(String name, CouponType expected) {
        assertEquals(expected, CouponType.valueOf(name));
    }
}
