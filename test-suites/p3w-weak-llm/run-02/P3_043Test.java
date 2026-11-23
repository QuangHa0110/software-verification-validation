package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void orderServiceCancelPendingOrderRestoresStockAndRefundsZero() {
        OrderService service = new OrderService();
        Product p = new Product("P", "Món", 10000L, 100, 1);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D1", List.of(new CartItem(p, 2)), 20000L, 0L, 15000L, 35000L, createdAt, Region.INNER_CITY);

        long refund = service.cancelOrder(order, createdAt.plusHours(100));

        assertEquals(0L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3, p.getStock());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, createdAt.plusHours(101)));
        assertEquals(3, p.getStock());
    }
}
