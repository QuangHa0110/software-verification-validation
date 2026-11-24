package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void orderConstructorValidationOrder() {
        Product p = new Product("P1", "Sản phẩm", 30000L, 200, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("", items, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("   ", items, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", null, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", List.of(), 0L, 0L, 15000L, 15000L, now, Region.INNER_CITY));
        List<CartItem> itemsWithNull = new ArrayList<>();
        itemsWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", itemsWithNull, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 59999L, 0L, 15000L, 74999L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, -1L, 15000L, 75001L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 60001L, 15000L, 14999L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, -1L, 59999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, 15000L, 75001L, now, Region.INNER_CITY));

        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, 15000L, 75000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("D1", items, 60000L, 0L, 15000L, 75000L, now, null));
    }
}
