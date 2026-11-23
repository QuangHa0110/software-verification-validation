package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void testOrderConstructorValidAndGetters() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 10, 0);
        Order order = new Order("ORD-01", items, 20000L, 5000L, 15000L, 30000L, now, Region.INNER_CITY);

        assertEquals("ORD-01", order.getId());
        assertEquals(1, order.getItems().size());
        assertEquals(20000L, order.getSubtotal());
        assertEquals(5000L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(30000L, order.getTotal());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());

        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }
}
