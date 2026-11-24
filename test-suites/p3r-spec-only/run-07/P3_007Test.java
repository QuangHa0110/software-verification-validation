package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t "})
    void couponRejectsBlankCode(String code) {
        LocalDate expiry = LocalDate.of(2000, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new Coupon(code, CouponType.FIXED, 1, 0, 0, expiry));
    }
}
