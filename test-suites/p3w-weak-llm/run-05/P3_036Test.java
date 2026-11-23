package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void testOrderConstructorValidation() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order("", items, 20000L, 0L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 20000L, 0L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", null, 20000L, 0L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", Collections.emptyList(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", Collections.singletonList(null), 20000L, 0L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 19999L, 0L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, -1L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 20001L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, -1L, 19999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, 15000L, 34999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, 15000L, 35000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, 15000L, 35000L, now, null));
    }
}
