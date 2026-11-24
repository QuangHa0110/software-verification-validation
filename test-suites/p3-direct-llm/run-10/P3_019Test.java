package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @ParameterizedTest
    @CsvSource({
        "19,0,0,19",
        "21,0,0,21",
        "20,-1,0,21",
        "20,21,2,1",
        "20,0,-1,19",
        "20,3,5,21",
        "20,3,5,23"
    })
    void orderConstructorRejectsEachInconsistentAmountIndependently(
            long subtotal, long discount, long shipping, long total) {
        Product p = new Product("A", "A", 10, 1, 2);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, subtotal, discount, shipping, total, t, Region.REMOTE));
        assertEquals(2, p.getStock());
        assertEquals(2, lines.get(0).getQuantity());
    }
}
