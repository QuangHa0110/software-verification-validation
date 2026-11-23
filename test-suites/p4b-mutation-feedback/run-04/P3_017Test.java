package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @ParameterizedTest
    @CsvSource({"19,0,0,19", "21,0,0,21", "20,-1,0,21", "20,21,1,0", "20,0,-1,19", "20,3,5,21", "20,3,5,23"})
    void directOrderRejectsInconsistentAmounts(long subtotal, long discount, long shipping, long total) {
        Product product = new Product("P", "Name", 10, 1, 7);
        List<CartItem> items = List.of(new CartItem(product, 2));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", items, subtotal, discount, shipping, total, time, Region.INNER_CITY));
        assertEquals(7, product.getStock());
    }
}
