package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_005Test {

    @Test
    void killOrderInitShippingZeroBoundary() {
        Product product = new Product("P1", "Product 1", 500_000L, 200, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // Test shipping == 0 boundary
        Order order = new Order("ORD-FREE", items, 500_000L, 0L, 0L, 500_000L, now, Region.INNER_CITY);
        assertEquals(0L, order.getShipping());
    }
}
