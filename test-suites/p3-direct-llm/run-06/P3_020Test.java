package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void orderConstructorRejectsInvalidIdentityItemsTimeAndRegion() {
        Product product = new Product("P", "P", 10, 1, 0);
        CartItem item = new CartItem(product, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.PROVINCE;

        for (String id : Arrays.asList(null, "", " \t")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(id, items, 20, 0, 0, 20, time, region));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 0, 0, 0, 0, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, time, region));
        List<CartItem> withNull = Arrays.asList(item, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 20, 0, 0, 20, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 0, 0, 20, null, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", items, 20, 0, 0, 20, time, null));
        assertEquals(0, product.getStock());
    }
}
