package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @Test
    void testOrderServiceCancelPendingRestoresStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 5);
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD-1", cart, null, Region.INNER_CITY, now);
        assertEquals(3, p.getStock());

        long refund = service.cancelOrder(order, now.plusHours(2));
        assertEquals(0L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.plusHours(3)));
    }
}
