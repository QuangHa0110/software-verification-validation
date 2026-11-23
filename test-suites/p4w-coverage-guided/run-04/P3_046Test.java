package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void orderServiceCancelOrderValidationAndOrderOfChecks() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Item", 10000L, 100, 5);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, now));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, now.minusSeconds(1)));

        service.pay(order);
        service.ship(order);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.minusSeconds(1)));
    }
}
