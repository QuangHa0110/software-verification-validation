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
        Region[] regions = Region.values();
        CouponType[] types = CouponType.values();
        OrderStatus[] statuses = OrderStatus.values();

        assertArrayEquals(new Region[] {
            Region.INNER_CITY, Region.SUBURB, Region.PROVINCE, Region.REMOTE
        }, regions);
        assertArrayEquals(new CouponType[] {CouponType.PERCENT, CouponType.FIXED}, types);
        assertArrayEquals(new OrderStatus[] {
            OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.CANCELLED
        }, statuses);

        for (Region region : regions) assertEquals(region, Region.valueOf(region.name()));
        for (CouponType type : types) assertEquals(type, CouponType.valueOf(type.name()));
        for (OrderStatus status : statuses) assertEquals(status, OrderStatus.valueOf(status.name()));

        regions[0] = null;
        types[0] = null;
        statuses[0] = null;
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
    }
}
