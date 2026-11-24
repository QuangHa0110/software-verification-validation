package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void orderConstructorRejectsInvalidIdentityLinesTimeOrRegionWithoutChangingStock() {
        Product p = new Product("P", "P", 10, 1, 1);
        CartItem item = new CartItem(p, 1);
        List<CartItem> lines = List.of(item);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.REMOTE;
        for (String blank : new String[] {null, "", " \t"}) {
            assertThrows(IllegalArgumentException.class,
                () -> new Order(blank, lines, 10, 0, 0, 10, t, region));
        }
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", null, 0, 0, 0, 0, t, region));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", List.of(), 0, 0, 0, 0, t, region));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", Arrays.asList(item, null), 10, 0, 0, 10, t, region));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, 10, 0, 0, 10, null, region));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, 10, 0, 0, 10, t, null));
        assertEquals(1, p.getStock());
    }
}
