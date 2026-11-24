package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @CsvSource({
        "19,0,2,21",
        "21,0,2,23",
        "20,-1,2,23",
        "20,21,2,1",
        "20,0,-1,19",
        "20,3,2,18",
        "20,3,2,20"
    })
    void orderConstructorChecksEachMonetaryConstraintIndependently(
            long subtotal, long discount, long shipping, long total) {
        Product p = new Product("P", "N", 10, 1, 0);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", lines, subtotal, discount, shipping, total, t, Region.SUBURB));
        assertEquals(0, p.getStock());
    }
}
