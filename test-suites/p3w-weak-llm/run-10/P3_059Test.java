package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_059Test {

    @Test
    void testCancelOrderValidationSequence() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "Item", 100L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 100L, 0L, 0L, 100L, t, Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, t));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        order.setStatus(OrderStatus.SHIPPED);
        LocalDateTime early = LocalDateTime.of(2026, 11, 23, 7, 59, 59);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, early));

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, early));

        order.setStatus(OrderStatus.PENDING);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, early));
    }
}
