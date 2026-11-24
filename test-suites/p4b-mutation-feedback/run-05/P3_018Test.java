package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void orderConstructorRejectsInvalidIdentityLinesAmountsTimeAndRegion() {
        Product product = new Product("P", "Book", 10, 1, 0);
        CartItem item = new CartItem(product, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.SUBURB;
        for (String blank : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(blank, items, 20, 2, 1, 19, time, region));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 20, 2, 1, 19, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, time, region));
        List<CartItem> withNull = Arrays.asList(item, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 20, 2, 1, 19, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 19, 2, 1, 18, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, -1, 1, 22, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 21, 1, 0, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 2, -1, 17, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 2, 1, 18, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 2, 1, 19, null, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 2, 1, 19, time, null));
        assertEquals(0, product.getStock());
        assertEquals(2, withNull.size());
        assertSame(product, withNull.get(0).getProduct());
        assertNull(withNull.get(1));
    }
}
