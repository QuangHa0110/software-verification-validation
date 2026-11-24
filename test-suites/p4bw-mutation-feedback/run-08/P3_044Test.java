package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @Test
    void orderSetStatus() {
        Product p = new Product("P1", "Product 1", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D1", items, 60000L, 6000L, 15000L, 69000L, now, Region.INNER_CITY);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }
}
