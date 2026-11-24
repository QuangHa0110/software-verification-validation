package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void enumValuesHaveDeclaredOrderAndEachCallReturnsAnIndependentArray() {
        Region[] regions = Region.values();
        CouponType[] types = CouponType.values();
        OrderStatus[] statuses = OrderStatus.values();
        regions[0] = null;
        types[0] = null;
        statuses[0] = null;

        assertArrayEquals(new Region[] {
            Region.INNER_CITY, Region.SUBURB, Region.PROVINCE, Region.REMOTE
        }, Region.values());
        assertArrayEquals(new CouponType[] {
            CouponType.PERCENT, CouponType.FIXED
        }, CouponType.values());
        assertArrayEquals(new OrderStatus[] {
            OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.CANCELLED
        }, OrderStatus.values());
    }
}
