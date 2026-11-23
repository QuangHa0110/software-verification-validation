package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void couponConstructorNullExpiryThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", CouponType.PERCENT, 10L, 0L, 0L, null));
    }
}
