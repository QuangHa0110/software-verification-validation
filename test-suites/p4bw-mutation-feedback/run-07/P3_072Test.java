package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_072Test {

    @Test
    void orderConstructorNullOrEmptyItemsThrowsException() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", null, 0L, 0L, 0L, 0L, now, Region.INNER_CITY)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", Collections.emptyList(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY)
        );
    }
}
