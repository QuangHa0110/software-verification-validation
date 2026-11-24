package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_042Test {

    @Test
    void orderServiceCancelOrderValidationOrder() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Món", 10000L, 100, 5);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D1", List.of(new CartItem(p, 1)), 10000L, 0L, 15000L, 25000L, createdAt, Region.INNER_CITY);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, createdAt));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));

        service.pay(order);
        service.ship(order);
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, createdAt.minusDays(1)));

        Order pendingOrder = new Order("D2", List.of(new CartItem(p, 1)), 10000L, 0L, 15000L, 25000L, createdAt, Region.INNER_CITY);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(pendingOrder, createdAt.minusSeconds(1)));
    }
}
