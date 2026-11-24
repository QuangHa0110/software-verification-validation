package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_047Test {

    @Test
    void testOrderServiceCancelValidation() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 5);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD-1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, now));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, now.minusSeconds(1)));

        service.pay(order);
        service.ship(order);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.plusHours(1)));
    }
}
