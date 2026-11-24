package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_040Test {

    @Test
    void orderServicePayOrderLifecycle() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Món", 10000L, 100, 5);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 10000L, 0L, 15000L, 25000L, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}
