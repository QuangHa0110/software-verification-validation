package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @CsvSource({
        "9,0,0,9", "11,0,0,11",
        "10,-1,0,11", "10,11,2,1",
        "10,0,-1,9", "10,3,5,11", "10,3,5,13"
    })
    void orderConstructorRejectsEachInconsistentMonetaryField(
            long subtotal, long discount, long shipping, long total) {
        Product product = new Product("P", "P", 5, 1, 0);
        List<CartItem> lines = List.of(new CartItem(product, 2));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, subtotal, discount, shipping, total, time, Region.REMOTE));
        assertEquals(0, product.getStock());
        assertEquals(2, lines.get(0).getQuantity());
    }
}
