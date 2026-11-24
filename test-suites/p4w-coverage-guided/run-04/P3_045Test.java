package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_045Test {

    @Test
    void orderServiceShipLifecycle() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Item", 10000L, 100, 5);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalStateException.class, () -> service.ship(order));

        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
