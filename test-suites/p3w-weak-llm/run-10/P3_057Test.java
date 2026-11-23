package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_057Test {

    @Test
    void testPayOrderLifecycle() {
        OrderService service = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        Product p = new Product("P", "Item", 100L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 100L, 0L, 0L, 100L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.pay(order));
    }
}
