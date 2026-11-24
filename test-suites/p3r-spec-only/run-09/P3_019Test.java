package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void orderConstructorRejectsInvalidIdentityLinesAmountsTimeAndRegion() {
        Product product = new Product("A", "A", 20, 1, 0);
        CartItem item = new CartItem(product, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.SUBURB;
        for (String id : new String[]{null, "", " \t"}) {
            assertThrows(IllegalArgumentException.class, () -> new Order(id, items, 40, 0, 0, 40, time, region));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, time, region));
        List<CartItem> withNull = Arrays.asList(item, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 40, 0, 0, 40, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 39, 0, 0, 39, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 41, 0, 0, 41, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, -1, 2, 43, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 41, 2, 1, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 0, -1, 39, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 3, 2, 38, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 3, 2, 40, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 0, 0, 40, null, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 40, 0, 0, 40, time, null));
        assertEquals(2, withNull.size());
        assertSame(product, withNull.get(0).getProduct());
        assertNull(withNull.get(1));
        assertEquals(0, product.getStock());
    }
}
