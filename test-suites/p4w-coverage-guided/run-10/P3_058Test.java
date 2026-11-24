package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_058Test {

    @Test
    void testShipOrderLifecycle() {
        OrderService service = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));

        Product p = new Product("P", "Item", 100L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 100L, 0L, 0L, 100L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
