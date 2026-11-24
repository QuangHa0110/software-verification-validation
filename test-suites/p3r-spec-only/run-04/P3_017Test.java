package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void orderConstructorRejectsEachInvalidFieldIndependently() {
        Product p = new Product("P", "P", 10, 1, 3);
        CartItem line = new CartItem(p, 2);
        List<CartItem> lines = List.of(line);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.SUBURB;
        for (String blank : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, lines, 20, 1, 2, 21, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, t, r));
        List<CartItem> withNull = Arrays.asList(line, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 20, 1, 2, 21, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 19, 1, 2, 20, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 21, 1, 2, 22, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, -1, 2, 23, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 21, 2, 1, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 1, -1, 18, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 1, 2, 20, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 1, 2, 21, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 1, 2, 21, t, null));
        assertEquals(3, p.getStock());
        assertEquals(2, line.getQuantity());
        assertEquals(2, withNull.size());
        assertNull(withNull.get(1));
    }
}
