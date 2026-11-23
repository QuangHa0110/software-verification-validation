package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_063Test {

    @Test
    void orderConstructorInvalidParametersThrow() {
        Product p = new Product("P1", "Prod 1", 10000L, 100, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 10, 0);

        // null/empty id
        assertThrows(IllegalArgumentException.class, () -> new Order("", items, 20000L, 5000L, 15000L, 30000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order(null, items, 20000L, 5000L, 15000L, 30000L, now, Region.INNER_CITY));

        // empty items
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", List.of(), 0L, 0L, 0L, 0L, now, Region.INNER_CITY));

        // subtotal mismatch
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 19999L, 5000L, 15000L, 29999L, now, Region.INNER_CITY));

        // invalid discount
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, -1L, 15000L, 35000L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 20001L, 15000L, 14999L, now, Region.INNER_CITY));

        // total mismatch
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 5000L, 15000L, 30001L, now, Region.INNER_CITY));

        // null createdAt or region
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 5000L, 15000L, 30000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 5000L, 15000L, 30000L, now, null));
    }
}
