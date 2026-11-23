package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @Test
    void orderSetStatus() {
        Product p = new Product("P1", "Sản phẩm", 10000L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 10000L, 0L, 15000L, 25000L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);

        assertEquals(OrderStatus.PENDING, order.getStatus());

        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        order.setStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
