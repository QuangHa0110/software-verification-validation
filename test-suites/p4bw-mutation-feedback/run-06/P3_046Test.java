package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void orderServiceShipTransitions() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);
        Order order = new Order("O1", items, 10000L, 0L, 15000L, 25000L, now, Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));

        assertThrows(IllegalStateException.class, () -> service.ship(order));

        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
