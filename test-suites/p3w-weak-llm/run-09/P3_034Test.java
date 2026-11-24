package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @Test
    void orderServiceCancelOrderValidationAndPreconditions() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Item", 100L, 10, 5);
        CartItem item = new CartItem(p, 1);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D", List.of(item), 100L, 0L, 15000L, 15100L, createdAt, Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, createdAt));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        order.setStatus(OrderStatus.SHIPPED);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, createdAt.minusHours(1)));

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, createdAt.minusHours(1)));

        order.setStatus(OrderStatus.PENDING);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, createdAt.minusSeconds(1)));

        order.setStatus(OrderStatus.PAID);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, createdAt.minusSeconds(1)));
    }
}
