package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void enumValuesAndValueOfRegion() {
        Region[] values = Region.values();
        assertEquals(4, values.length);
        assertSame(Region.INNER_CITY, values[0]);
        assertSame(Region.SUBURB, values[1]);
        assertSame(Region.PROVINCE, values[2]);
        assertSame(Region.REMOTE, values[3]);
        assertSame(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertSame(Region.SUBURB, Region.valueOf("SUBURB"));
        assertSame(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertSame(Region.REMOTE, Region.valueOf("REMOTE"));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("UNKNOWN"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
    }
}
