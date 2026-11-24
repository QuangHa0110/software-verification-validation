package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_054Test {

    @Test
    void orderServiceCancelPendingOrderRestoresStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Item", 100L, 1, 5);
        cart.addItem(p, 2);
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, created);
        assertEquals(3, p.getStock());

        long refund = service.cancelOrder(order, created);
        assertEquals(0L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, created.plusHours(1)));
        assertEquals(5, p.getStock());
    }
}
