package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void enumRegionValuesAndOrder() {
        Region[] values = Region.values();
        assertEquals(4, values.length);
        assertEquals(Region.INNER_CITY, values[0]);
        assertEquals(Region.SUBURB, values[1]);
        assertEquals(Region.PROVINCE, values[2]);
        assertEquals(Region.REMOTE, values[3]);
        values[0] = Region.REMOTE;
        assertEquals(Region.INNER_CITY, Region.values()[0]);
    }
}
