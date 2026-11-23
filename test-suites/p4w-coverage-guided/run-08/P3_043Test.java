package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void orderConstructorValidation() {
        Product p = new Product("P1", "Product 1", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 60000L, 6000L, 15000L, 69000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("  ", items, 60000L, 6000L, 15000L, 69000L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", null, 60000L, 6000L, 15000L, 69000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", Collections.emptyList(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));
        List<CartItem> itemsWithNull = new ArrayList<>();
        itemsWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", itemsWithNull, 0L, 0L, 0L, 0L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 59999L, 6000L, 15000L, 68999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, -1L, 15000L, 75001L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 60001L, 15000L, 14999L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 6000L, -1L, 53999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 6000L, 15000L, 69001L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 6000L, 15000L, 69000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 6000L, 15000L, 69000L, now, null));
    }
}
