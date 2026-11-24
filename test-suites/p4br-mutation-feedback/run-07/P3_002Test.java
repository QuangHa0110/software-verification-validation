package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void enumLookupRequiresAnExactNonNullName() {
        for (Region region : Region.values()) {
            assertEquals(region, Region.valueOf(region.name()));
        }
        for (CouponType type : CouponType.values()) {
            assertEquals(type, CouponType.valueOf(type.name()));
        }
        for (OrderStatus status : OrderStatus.values()) {
            assertEquals(status, OrderStatus.valueOf(status.name()));
        }

        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("suburb"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf(" FIXED"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("PAID "));
    }
}
