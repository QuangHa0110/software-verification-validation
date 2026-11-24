package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_074Test {

    @Test
    void orderServiceCancelOrderInvalidStatesAndTimestamps() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 50000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, now));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, now.minusMinutes(1)));

        service.pay(order);
        service.ship(order);
        // cannot cancel shipped order
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.plusHours(1)));
    }
}
