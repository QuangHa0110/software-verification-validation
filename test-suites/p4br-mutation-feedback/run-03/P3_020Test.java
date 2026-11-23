package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @ParameterizedTest
    @CsvSource({
        "19,0,0,19", "21,0,0,21", "20,-1,0,21", "20,21,5,4",
        "20,0,-1,19", "20,7,3,15", "20,7,3,17"
    })
    void directOrderConstructorRejectsIndividuallyInconsistentAmounts(
            long subtotal, long discount, long shipping, long total) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "N", 10, 1, 2);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, subtotal, discount, shipping, total, t, Region.INNER_CITY));
        assertSame(p, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertEquals(2, p.getStock());
    }
}
