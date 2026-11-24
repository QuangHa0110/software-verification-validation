package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void orderConstructorRejectsEachInvalidFieldAndInconsistentAmount() {
        Product p = new Product("P", "P", 10, 3, 1);
        CartItem line = new CartItem(p, 1);
        List<CartItem> lines = List.of(line);
        Region r = Region.SUBURB;
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        for (String missing : Arrays.asList(null, "", "\t ")) {
            assertThrows(IllegalArgumentException.class, () -> new Order(missing, lines, 10, 0, 0, 10, t, r));
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
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 2, 1, 10, t, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 0, 0, 10, null, r));
        assertThrows(IllegalArgumentException.class, () -> new Order("O", lines, 10, 0, 0, 10, t, null));
        assertEquals(1, p.getStock());
    }
}
