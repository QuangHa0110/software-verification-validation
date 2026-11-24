package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4_002Test {

    @Test
    void orderConstructorNegativeShippingThrows() {
        Product product = new Product("P1", "Item", 10000, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
                new Order("ORD1", items, 10000, 0, -1, 9999, now, Region.INNER_CITY));
    }
}
