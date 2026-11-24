package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_048Test {

    @Test
    void orderServiceCancelOrderPendingRestoresStockAndReturnsZeroRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Pen", 10000L, 50, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, created);
        assertEquals(3, p.getStock());

        long refund = service.cancelOrder(order, created.plusDays(2));
        assertEquals(0L, refund);
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, created.plusDays(3)));
        assertEquals(5, p.getStock());
    }
}
