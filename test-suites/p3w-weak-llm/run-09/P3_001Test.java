package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void enumValuesAndValueOfCoverage() {
        Region[] regions = Region.values();
        assertEquals(4, regions.length);
        assertEquals(Region.INNER_CITY, regions[0]);
        assertEquals(Region.SUBURB, regions[1]);
        assertEquals(Region.PROVINCE, regions[2]);
        assertEquals(Region.REMOTE, regions[3]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("INVALID"));

        CouponType[] types = CouponType.values();
        assertEquals(2, types.length);
        assertEquals(CouponType.PERCENT, types[0]);
        assertEquals(CouponType.FIXED, types[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("invalid"));

        OrderStatus[] statuses = OrderStatus.values();
        assertEquals(4, statuses.length);
        assertEquals(OrderStatus.PENDING, statuses[0]);
        assertEquals(OrderStatus.PAID, statuses[1]);
        assertEquals(OrderStatus.SHIPPED, statuses[2]);
        assertEquals(OrderStatus.CANCELLED, statuses[3]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("pending"));
    }
}
