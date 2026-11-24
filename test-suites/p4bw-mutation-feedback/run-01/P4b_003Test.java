package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void killCouponInitPercentValue100Boundary() {
        Coupon coupon = new Coupon("C100", CouponType.PERCENT, 100L, 0L, 0L, LocalDate.of(2026, 11, 23));
        assertEquals(100L, coupon.getValue());
    }
}
