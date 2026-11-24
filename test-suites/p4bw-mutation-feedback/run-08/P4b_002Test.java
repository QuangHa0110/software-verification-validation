package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_002Test {

    @Test
    void killOrderConstructorDiscountBoundaryMutant() {
        Product product = new Product("P1", "Pen", 10_000L, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        long subtotal = 10_000L;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // Boundary check: discount == 0 is valid
        Order validZeroDiscount = new Order("ORD-1", items, subtotal, 0L, 15_000L, subtotal + 15_000L, now, Region.INNER_CITY);
        assertEquals(0L, validZeroDiscount.getDiscount());

        // Boundary check: discount == subtotal is valid
        Order validFullDiscount = new Order("ORD-2", items, subtotal, subtotal, 15_000L, 15_000L, now, Region.INNER_CITY);
        assertEquals(subtotal, validFullDiscount.getDiscount());
    }
}
