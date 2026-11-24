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
        OrderStatus[] statuses = {
            OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.CANCELLED
        };

        assertArrayEquals(regions, Region.values());
        assertArrayEquals(types, CouponType.values());
        assertArrayEquals(statuses, OrderStatus.values());

        Region.values()[0] = null;
        CouponType.values()[0] = null;
        OrderStatus.values()[0] = null;
        assertArrayEquals(regions, Region.values());
        assertArrayEquals(types, CouponType.values());
        assertArrayEquals(statuses, OrderStatus.values());
    }
}
