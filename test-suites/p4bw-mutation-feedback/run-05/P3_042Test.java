package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_042Test {

    @Test
    void testOrderServicePayTransitions() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 5);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD-1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.pay(order));
    }
}
