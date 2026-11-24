package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @Test
    void orderServiceShipOrderLifecycle() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Món", 10000L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 10000L, 0L, 15000L, 25000L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());

        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }
}
