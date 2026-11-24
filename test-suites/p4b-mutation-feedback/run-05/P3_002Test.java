package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void enumLookupRejectsNullAndInexactNames() {
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        for (String name : List.of("remote", " REMOTE", "UNKNOWN")) {
            assertThrows(IllegalArgumentException.class, () -> Region.valueOf(name));
        }
        for (String name : List.of("fixed", "FIXED ", "")) {
            assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf(name));
        }
        for (String name : List.of("paid", " PAID", "UNKNOWN")) {
            assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf(name));
        }
    }
}
