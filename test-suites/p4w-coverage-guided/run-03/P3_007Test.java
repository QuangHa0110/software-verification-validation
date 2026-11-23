package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @ParameterizedTest
    @ValueSource(strings = {"percent", "fixed", "OTHER", ""})
    void couponTypeEnumValueOfInvalid(String name) {
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf(name));
    }
}
