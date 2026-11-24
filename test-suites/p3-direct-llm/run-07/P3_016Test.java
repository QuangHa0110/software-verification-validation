package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void orderConstructorRejectsMissingMetadataAndInvalidItemLists() {
        Product p = new Product("P", "Name", 11, 1, 4);
        CartItem line = new CartItem(p, 2);
        List<CartItem> lines = List.of(line);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Region region = Region.SUBURB;
        for (String id : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(id, lines, 22, 0, 0, 22, time, region));
        }
        assertThrows(IllegalArgumentException.class, () -> new Order("D", null, 22, 0, 0, 22, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", List.of(), 0, 0, 0, 0, time, region));
        List<CartItem> withNull = Arrays.asList(line, null);
        assertThrows(IllegalArgumentException.class, () -> new Order("D", withNull, 22, 0, 0, 22, time, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 22, 0, 0, 22, null, region));
        assertThrows(IllegalArgumentException.class, () -> new Order("D", lines, 22, 0, 0, 22, time, null));
        assertEquals(2, withNull.size());
        assertSame(p, withNull.get(0).getProduct());
        assertEquals(2, withNull.get(0).getQuantity());
        assertNull(withNull.get(1));
        assertEquals(4, p.getStock());
    }
}
