package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_050Test {

    @Test
    void testOrderSetStatus() {
        Product p = new Product("P1", "Pen", 100L, 10, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 100L, 0L, 0L, 100L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }
}
