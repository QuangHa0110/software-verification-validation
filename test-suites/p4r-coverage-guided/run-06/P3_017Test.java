package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void directOrderConstructorRejectsEachInvalidArgumentWithoutMutatingInputs() {
        Product p = new Product("A", "A", 10, 1, 2);
        CartItem line = new CartItem(p, 2);
        List<CartItem> lines = new ArrayList<>(List.of(line));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.REMOTE;

        for (String blank : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, lines, 20, 0, 0, 20, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", Arrays.asList(line, null), 20, 0, 0, 20, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 19, 0, 0, 19, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 21, 0, 0, 21, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, -1, 0, 21, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 21, 5, 4, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, -1, 19, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 3, 4, 20, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 3, 4, 21, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 3, 4, 21, t, null));

        assertEquals(1, lines.size());
        assertSame(p, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertEquals(2, p.getStock());
    }
}
