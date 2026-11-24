package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void orderConstructorRejectsInvalidIdentifiersLinesAmountsTimeAndRegion() {
        Product p = new Product("P", "Name", 10, 1, 2);
        CartItem item = new CartItem(p, 2);
        List<CartItem> lines = List.of(item);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.SUBURB;

        for (String blank : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, lines, 20, 0, 0, 20, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, t, r));
        List<CartItem> withNull = Arrays.asList(item, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 20, 0, 0, 20, t, r));

        for (long wrongSubtotal : new long[]{19, 21}) {
            assertThrows(IllegalArgumentException.class,
                () -> new Order("D", lines, wrongSubtotal, 0, 0, wrongSubtotal, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, -1, 0, 21, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 21, 2, 1, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, -1, 19, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 3, 5, 21, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 3, 5, 23, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, 0, 20, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, 0, 20, t, null));
        assertEquals(2, p.getStock());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(p, lines.get(0).getProduct());
    }
}
