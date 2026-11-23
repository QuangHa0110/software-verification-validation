package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void orderConstructorValidationRules() {
        Product p = new Product("P1", "Pen", 10000L, 100, 10);
        CartItem item = new CartItem(p, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("", items, 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("   ", items, 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));

        List<CartItem> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 10000L, 0L, 0L, 10000L, time, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 9999L, 0L, 0L, 9999L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, -1L, 0L, 10001L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, 10001L, 0L, -1L, time, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, 0L, -1L, 9999L, time, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, 0L, 15000L, 24999L, time, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, 0L, 0L, 10000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10000L, 0L, 0L, 10000L, time, null));
    }
}
