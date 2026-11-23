package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void orderConstructorInvalidFinancials() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 19999L, 0L, 15000L, 34999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, -1L, 15000L, 35001L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 20001L, 15000L, 14999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, -1L, 19999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, 15000L, 34999L, now, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 20000L, 0L, 15000L, 35001L, now, Region.INNER_CITY));
    }
}
