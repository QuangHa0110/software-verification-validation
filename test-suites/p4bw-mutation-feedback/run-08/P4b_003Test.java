package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void killOrderConstructorShippingBoundaryMutant() {
        Product product = new Product("P1", "Pen", 10_000L, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        long subtotal = 10_000L;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // Boundary check: shipping == 0 is valid (total == subtotal - discount + 0)
        Order validZeroShipping = new Order("ORD-1", items, subtotal, 0L, 0L, subtotal, now, Region.INNER_CITY);
        assertEquals(0L, validZeroShipping.getShipping());
    }
}
