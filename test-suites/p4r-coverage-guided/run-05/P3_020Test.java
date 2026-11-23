package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void directOrderConstructionRejectsInvalidIdItemsAndMissingMetadata() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.REMOTE;
        Product p = new Product("P", "Pen", 5, 1, 7);
        CartItem row = new CartItem(p, 2);
        List<CartItem> rows = List.of(row);
        for (String id : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Order(id, rows, 10, 1, 3, 12, t, region));
        }
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", null, 0, 0, 0, 0, t, region));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", List.of(), 0, 0, 0, 0, t, region));
        List<CartItem> withNull = Arrays.asList(row, null);
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", withNull, 10, 1, 3, 12, t, region));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", rows, 10, 1, 3, 12, null, region));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", rows, 10, 1, 3, 12, t, null));
        assertEquals(2, withNull.size());
        assertSame(p, withNull.get(0).getProduct());
        assertEquals(2, withNull.get(0).getQuantity());
        assertNull(withNull.get(1));
        assertEquals(7, p.getStock());
    }
}
