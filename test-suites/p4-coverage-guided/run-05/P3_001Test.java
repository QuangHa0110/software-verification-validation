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

        Region[] firstRegions = Region.values();
        CouponType[] firstTypes = CouponType.values();
        OrderStatus[] firstStatuses = OrderStatus.values();
        assertArrayEquals(regions, firstRegions);
        assertArrayEquals(types, firstTypes);
        assertArrayEquals(statuses, firstStatuses);

        firstRegions[0] = null;
        firstTypes[0] = null;
        firstStatuses[0] = null;
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
    }
}
