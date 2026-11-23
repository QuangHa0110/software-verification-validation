package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void enumLookupRejectsNullAndRequiresExactSpelling() {
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));

        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("suburb"));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("REMOTE "));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("fixed"));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf(" PERCENT"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("paid"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("SHIPPED "));
    }
}
