package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @ParameterizedTest
    @CsvSource({"9,0,2,11", "10,-1,2,13", "10,11,2,1", "10,0,-1,9", "10,3,2,8"})
    void orderRejectsEachInconsistentAmount(long subtotal, long discount, long shipping, long total) {
        Product p = new Product("P", "P", 5, 1, 2);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, subtotal, discount, shipping, total, t, Region.SUBURB));
        assertEquals(2, p.getStock());
        assertEquals(2, lines.get(0).getQuantity());
    }
}
