package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_052Test {

    @Test
    void orderServiceShipOrderLifecycle() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Item", 100L, 1, 5);
        cart.addItem(p, 1);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, LocalDateTime.of(2026, 11, 23, 8, 0));

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalStateException.class, () -> service.ship(order));

        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
