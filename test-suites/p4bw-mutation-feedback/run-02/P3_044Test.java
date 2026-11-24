package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @Test
    void orderServiceCancelPaidOrderWithin24HoursFullRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Món", 250000L, 500, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D1", List.of(new CartItem(p, 2)), 500000L, 1L, 15000L, 514999L, createdAt, Region.INNER_CITY);

        service.pay(order);
        long refund = service.cancelOrder(order, createdAt.plusHours(24));

        assertEquals(514999L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, p.getStock());
    }
}
