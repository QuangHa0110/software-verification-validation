package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @Test
    void orderServiceShipStateTransitions() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Item", 100L, 10, 5);
        CartItem item = new CartItem(p, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));

        Order order = new Order("D", List.of(item), 100L, 0L, 15000L, 15100L, time, Region.INNER_CITY);

        assertThrows(IllegalStateException.class, () -> service.ship(order));

        order.setStatus(OrderStatus.PAID);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
