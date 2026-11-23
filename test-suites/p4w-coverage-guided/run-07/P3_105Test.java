package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_105Test {

    @Test
    void enumValuesAndValueOfCoverage() {
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(4, Region.values().length);

        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(2, CouponType.values().length);

        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(4, OrderStatus.values().length);
    }
}
