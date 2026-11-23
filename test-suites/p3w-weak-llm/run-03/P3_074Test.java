package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_074Test {

    @Test
    void orderNullOrEmptyItems() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("D1", null, 0L, 0L, 0L, 0L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () ->
            new Order("D1", List.of(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));
    }
}
