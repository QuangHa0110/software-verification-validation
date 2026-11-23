package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void directOrderConstructorRejectsEachInvalidArgumentAndInconsistentAmount() {
        Product p = new Product("A", "A", 15, 2, 4);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = new ArrayList<>(List.of(item));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.PROVINCE;
        for (String blank : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, items, 30, 7, 2, 25, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, t, r));
        List<CartItem> withNull = Arrays.asList(item, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 30, 7, 2, 25, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 29, 7, 2, 24, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 31, 7, 2, 26, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, -1, 2, 33, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, 31, 2, 1, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, 7, -1, 22, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, 7, 2, 24, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, 7, 2, 25, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 30, 7, 2, 25, t, null));
        assertEquals(1, items.size());
        assertSame(p, items.get(0).getProduct());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals(4, p.getStock());
    }
}
