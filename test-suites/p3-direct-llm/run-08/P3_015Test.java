package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void orderConstructorRejectsInvalidIdentifiersLinesAmountsTimeAndRegion() {
        Product p = new Product("P", "P", 5, 1, 2);
        CartItem line = new CartItem(p, 2);
        List<CartItem> lines = List.of(line);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region r = Region.PROVINCE;
        for (String id : new String[]{null, "", "\t "}) {
            assertThrows(IllegalArgumentException.class, () -> new Order(id, lines, 10, 0, 0, 10, t, r));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("O", null, 0, 0, 0, 0, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", List.of(), 0, 0, 0, 0, t, r));
        List<CartItem> withNull = Arrays.asList(line, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("O", withNull, 10, 0, 0, 10, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 9, 0, 0, 9, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 11, 0, 0, 11, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, -1, 0, 11, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 11, 2, 1, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 0, -1, 9, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 1, 2, 10, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 0, 0, 10, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 0, 0, 10, t, null));
        assertEquals(2, p.getStock());
        assertEquals(2, line.getQuantity());
        assertSame(p, line.getProduct());
        assertNull(withNull.get(1));
    }
}
