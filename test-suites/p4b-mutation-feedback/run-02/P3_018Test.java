package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void directOrderRejectsInvalidIdentityLinesTimestampAndRegion() {
        Product product = new Product("P", "P", 10, 1, 1);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.REMOTE;
        for (String id : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(id, items, 10, 0, 0, 10, time, region));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, time, region));
        List<CartItem> withNull = new ArrayList<>(Arrays.asList(item, null));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 10, 0, 0, 10, time, region));
        assertEquals(2, withNull.size());
        assertSame(product, withNull.get(0).getProduct());
        assertEquals(1, withNull.get(0).getQuantity());
        assertNull(withNull.get(1));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10, 0, 0, 10, null, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 10, 0, 0, 10, time, null));
        assertEquals(1, product.getStock());
    }
}
