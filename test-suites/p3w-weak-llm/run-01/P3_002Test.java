package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void regionEnumValueOfValidAndInvalid() {
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("UNKNOWN"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
    }
}
