package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void orderConstructorRejectsMissingIdentityLinesTimeOrRegion() {
        Product p = new Product("P", "N", 10, 1, 4);
        CartItem line = new CartItem(p, 2);
        List<CartItem> lines = List.of(line);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.REMOTE;
        for (String blank : new String[] {null, "", "\t "}) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, lines, 20, 0, 0, 20, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, t, r));
        List<CartItem> withNull = Arrays.asList(line, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 20, 0, 0, 20, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, 0, 20, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 20, 0, 0, 20, t, null));
        assertEquals(2, withNull.size());
        assertSame(p, withNull.get(0).getProduct());
        assertEquals(2, withNull.get(0).getQuantity());
        assertNull(withNull.get(1));
        assertEquals(4, p.getStock());
    }
}
