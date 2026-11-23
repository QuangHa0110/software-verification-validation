package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_047Test {

    @Test
    void orderServiceCancelOrderValidationAndPrecedence() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0, 0);
        Order order = new Order("O1", items, 10000L, 0L, 15000L, 25000L, created, Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, created));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        order.setStatus(OrderStatus.SHIPPED);
        LocalDateTime before = created.minusHours(1);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, before));

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, before));

        order.setStatus(OrderStatus.PENDING);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, before));

        order.setStatus(OrderStatus.PAID);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, before));
    }
}
