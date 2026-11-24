package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4_003Test {

    @Test
    void orderServiceCancelOrderCancelledStatusThrows() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product product = new Product("P1", "Item", 10000, 100, 10);
        cart.addItem(product, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD1", cart, null, Region.INNER_CITY, now);
        service.cancelOrder(order, now);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now));
    }
}
