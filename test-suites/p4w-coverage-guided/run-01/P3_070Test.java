package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_070Test {

    @Test
    void orderServiceShipSuccessAndInvalidStates() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 50000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.ship(null));

        // shipping PENDING order throws
        assertThrows(IllegalStateException.class, () -> service.ship(order));

        service.pay(order);
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        // shipping already shipped order throws
        assertThrows(IllegalStateException.class, () -> service.ship(order));
    }
}
