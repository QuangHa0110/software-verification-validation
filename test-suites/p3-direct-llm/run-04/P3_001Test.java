package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void enumValuesPreserveDeclarationOrderAndReturnIndependentArrays() {
        Region[] regions = {Region.INNER_CITY, Region.SUBURB, Region.PROVINCE, Region.REMOTE};
        CouponType[] types = {CouponType.PERCENT, CouponType.FIXED};
        OrderStatus[] statuses = {OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.CANCELLED};

        Region.values()[0] = null;
        CouponType.values()[0] = null;
        OrderStatus.values()[0] = null;
        assertArrayEquals(regions, Region.values());
        assertArrayEquals(types, CouponType.values());
        assertArrayEquals(statuses, OrderStatus.values());

        for (Region region : regions) {
            assertEquals(region, Region.valueOf(region.name()));
        }
        for (CouponType type : types) {
            assertEquals(type, CouponType.valueOf(type.name()));
        }
        for (OrderStatus status : statuses) {
            assertEquals(status, OrderStatus.valueOf(status.name()));
        }

        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("remote"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf(" FIXED"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("PAID "));
    }
}
