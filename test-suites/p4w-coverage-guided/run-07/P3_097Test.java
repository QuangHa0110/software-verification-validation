package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_097Test {

    @Test
    void orderServiceCancelOrderNullOrderOrNowThrowsException() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, now));

        Product p = new Product("P01", "Book", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Order order = service.placeOrder("ORD1", cart, null, Region.INNER_CITY, now);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
    }
}
