package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void regionEnumValuesAndValueOf() {
        Region[] regions = Region.values();
        assertEquals(4, regions.length);
        assertEquals(Region.INNER_CITY, regions[0]);
        assertEquals(Region.SUBURB, regions[1]);
        assertEquals(Region.PROVINCE, regions[2]);
        assertEquals(Region.REMOTE, regions[3]);

        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));

        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("UNKNOWN"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
    }
}
