package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_061Test {

    @Test
    void testCancelPaidOrderWithin24HoursFullRefund() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "Item", 250000L, 500, 0);
        Order order = new Order("D1", List.of(new CartItem(p, 2)), 500000L, 1L, 15000L, 514999L, t, Region.INNER_CITY);
        order.setStatus(OrderStatus.PAID);

        LocalDateTime exact24h = LocalDateTime.of(2026, 11, 24, 8, 0, 0);
        long refund = service.cancelOrder(order, exact24h);
        assertEquals(514999L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, p.getStock());
    }
}
