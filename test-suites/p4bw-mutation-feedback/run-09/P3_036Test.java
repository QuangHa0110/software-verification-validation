package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void orderServiceCancelOrderPaidWithin24Hours() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Item", 250000L, 10, 0);
        CartItem item = new CartItem(p, 2);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D", List.of(item), 500000L, 1L, 15000L, 514999L, createdAt, Region.INNER_CITY);
        order.setStatus(OrderStatus.PAID);

        long refund = service.cancelOrder(order, createdAt.plusHours(24));
        assertEquals(514999L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, p.getStock());
    }
}
