package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_034Test {

    @Test
    void orderConstructorDiscountBoundariesZeroAndFullSubtotal() {
        Product product = new Product("P1", "Product 1", 10_000L, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // discount == 0 boundary (0 <= discount <= subtotal)
        Order orderZeroDiscount = new Order("ORD-01", items, 10_000L, 0L, 15_000L, 25_000L, now, Region.INNER_CITY);
        assertEquals(0L, orderZeroDiscount.getDiscount());
        assertEquals(25_000L, orderZeroDiscount.getTotal());

        // discount == subtotal boundary
        Order orderFullDiscount = new Order("ORD-02", items, 10_000L, 10_000L, 15_000L, 15_000L, now, Region.INNER_CITY);
        assertEquals(10_000L, orderFullDiscount.getDiscount());
        assertEquals(15_000L, orderFullDiscount.getTotal());
    }
}
