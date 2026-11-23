package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_053Test {

    @Test
    void orderServiceCancelOrderValidationAndPrecedence() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Item", 100L, 1, 5);
        cart.addItem(p, 1);
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, created);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, created));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        LocalDateTime earlyTime = LocalDateTime.of(2026, 11, 23, 7, 59, 59);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, earlyTime));

        service.pay(order);
        service.ship(order);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, earlyTime));
    }
}
