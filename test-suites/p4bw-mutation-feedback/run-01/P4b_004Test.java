package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_004Test {

    @Test
    void killOrderInitDiscountZeroAndEqualSubtotalBoundary() {
        Product product = new Product("P1", "Product 1", 50_000L, 200, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // Test discount == 0 boundary
        Order orderZeroDiscount = new Order("ORD-0", items, 50_000L, 0L, 15_000L, 65_000L, now, Region.INNER_CITY);
        assertEquals(0L, orderZeroDiscount.getDiscount());

        // Test discount == subtotal boundary
        Order orderFullDiscount = new Order("ORD-FULL", items, 50_000L, 50_000L, 15_000L, 15_000L, now, Region.INNER_CITY);
        assertEquals(50_000L, orderFullDiscount.getDiscount());
    }
}
