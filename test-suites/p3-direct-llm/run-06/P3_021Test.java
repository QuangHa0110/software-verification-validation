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
        "19,0,0,19", "21,0,0,21", "20,-1,0,21", "20,21,2,1",
        "20,0,-1,19", "20,3,5,21", "20,3,5,23"
    })
    void orderConstructorRejectsEachInconsistentAmount(
            long subtotal, long discount, long shipping, long total) {
        Product product = new Product("P", "P", 10, 1, 3);
        List<CartItem> items = new ArrayList<>(List.of(new CartItem(product, 2)));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
            () -> new Order("D", items, subtotal, discount, shipping, total, time, Region.SUBURB));
        assertEquals(1, items.size());
        assertSame(product, items.get(0).getProduct());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals(3, product.getStock());
    }
}
