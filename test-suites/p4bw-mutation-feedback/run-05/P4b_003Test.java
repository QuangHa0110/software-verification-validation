package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void testKillOrderConstructorDiscountNegativeBoundary() {
        Product product = new Product("P1", "Product 1", 1000, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        long subtotal = 1000;
        long discount = -1;
        long shipping = 0;
        long total = subtotal - discount + shipping;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, subtotal, discount, shipping, total, now, Region.INNER_CITY)
        );
    }
}
