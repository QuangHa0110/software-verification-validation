package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_048Test {

    @Test
    void testOrderConstructorValidationFailures() {
        Product p = new Product("P1", "Pen", 100L, 10, 5);
        CartItem item = new CartItem(p, 1);
        List<CartItem> validItems = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order(null, validItems, 100L, 0L, 0L, 100L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("  ", validItems, 100L, 0L, 0L, 100L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", null, 100L, 0L, 0L, 100L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", List.of(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));
        List<CartItem> itemsWithNull = new ArrayList<>();
        itemsWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", itemsWithNull, 0L, 0L, 0L, 0L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 99L, 0L, 0L, 99L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, -1L, 0L, 100L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, 101L, 0L, 0L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, 0L, -1L, 99L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, 0L, 10L, 109L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, 0L, 10L, 110L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", validItems, 100L, 0L, 10L, 110L, now, null));
    }
}
