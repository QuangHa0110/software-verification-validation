package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_034Test {

    @Test
    void couponConstructorPercentBoundaryValueOneHundredSucceeds() {
        Coupon coupon = new Coupon("C100", CouponType.PERCENT, 100L, 0L, 0L, LocalDate.of(2026, 12, 31));
        assertEquals(100L, coupon.getValue());
        assertEquals(CouponType.PERCENT, coupon.getType());
    }
}
