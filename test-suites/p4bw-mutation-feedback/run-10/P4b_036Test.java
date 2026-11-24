package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_036Test {

    @Test
    void testOrderConstructorDiscountBoundaryAndConditionals() {
        Product p = new Product("P1", "Product 1", 1000L, 100, 10);
        CartItem item = new CartItem(p, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // subtotal == lineSum (1000L), discount < 0 (-1L)
        assertThrows(IllegalArgumentException.class, () -> 
            new Order("ORD-1", items, 1000L, -1L, 15000L, 1000L - (-1L) + 15000L, now, Region.INNER_CITY)
        );

        // subtotal == lineSum (1000L), discount > subtotal (1001L)
        assertThrows(IllegalArgumentException.class, () -> 
            new Order("ORD-2", items, 1000L, 1001L, 15000L, 1000L - 1001L + 15000L, now, Region.INNER_CITY)
        );

        // Boundary cases: discount == 0 and discount == subtotal should succeed
        Order orderZeroDiscount = new Order("ORD-3", items, 1000L, 0L, 15000L, 16000L, now, Region.INNER_CITY);
        assertEquals(0L, orderZeroDiscount.getDiscount());

        Order orderMaxDiscount = new Order("ORD-4", items, 1000L, 1000L, 15000L, 15000L, now, Region.INNER_CITY);
        assertEquals(1000L, orderMaxDiscount.getDiscount());
    }
}
