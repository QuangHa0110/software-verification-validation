package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @ParameterizedTest
    @CsvSource({
        "9,1,3,11", "11,1,3,13", "10,-1,3,14", "10,11,3,2",
        "10,0,-1,9", "10,1,3,11", "10,1,3,13"
    })
    void directOrderConstructionRejectsEachIndependentMoneyInconsistency(
            long subtotal, long discount, long shipping, long total) {
        Product p = new Product("P", "Pen", 5, 1, 2);
        List<CartItem> rows = List.of(new CartItem(p, 2));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", rows, subtotal, discount, shipping, total, t, Region.SUBURB));
        assertEquals(2, p.getStock());
        assertSame(p, rows.get(0).getProduct());
        assertEquals(2, rows.get(0).getQuantity());
    }
}
