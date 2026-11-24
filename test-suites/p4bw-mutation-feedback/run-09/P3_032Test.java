package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void orderServicePayStateTransitions() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Item", 100L, 10, 5);
        CartItem item = new CartItem(p, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        Order order = new Order("D", List.of(item), 100L, 0L, 15000L, 15100L, time, Region.INNER_CITY);
        assertEquals(OrderStatus.PENDING, order.getStatus());

        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.pay(order));

        order.setStatus(OrderStatus.SHIPPED);
        assertThrows(IllegalStateException.class, () -> service.pay(order));

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.pay(order));
    }
}
