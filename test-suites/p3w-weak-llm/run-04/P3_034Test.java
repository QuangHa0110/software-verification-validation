package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @Test
    void orderConstructorInvalidParameters() {
        Product p = new Product("P1", "Item", 30000L, 500, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 60000L, 0L, 0L, 60000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("", items, 60000L, 0L, 0L, 60000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("   ", items, 60000L, 0L, 0L, 60000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", null, 60000L, 0L, 0L, 60000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", List.of(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));

        List<CartItem> itemsWithNull = new ArrayList<>();
        itemsWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", itemsWithNull, 0L, 0L, 0L, 0L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 59999L, 0L, 0L, 59999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, -1L, 0L, 60001L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 60001L, 0L, 0L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, -1L, 59999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 6000L, 15000L, 68999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, 0L, 60000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, 0L, 60000L, now, null));
    }
}
