package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void orderServiceCancelOrderPendingAndRestoresStock() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Item", 100L, 10, 0);
        CartItem item = new CartItem(p, 2);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D", List.of(item), 200L, 0L, 15000L, 15200L, createdAt, Region.INNER_CITY);

        long refund1 = service.cancelOrder(order, createdAt);
        assertEquals(0L, refund1);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, p.getStock());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, createdAt));
        assertEquals(2, p.getStock());

        Product p2 = new Product("P2", "Item2", 100L, 10, 1);
        Order order2 = new Order("D2", List.of(new CartItem(p2, 3)), 300L, 0L, 0L, 300L, createdAt, Region.INNER_CITY);
        long refund2 = service.cancelOrder(order2, createdAt.plusHours(48));
        assertEquals(0L, refund2);
        assertEquals(OrderStatus.CANCELLED, order2.getStatus());
        assertEquals(4, p2.getStock());
    }
}
