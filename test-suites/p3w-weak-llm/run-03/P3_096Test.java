package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_096Test {

    @Test
    void orderServiceCancelPendingOrderRestoresStockZeroRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Bút", 250000L, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D1", cart, null, Region.INNER_CITY, now);
        assertEquals(0, p.getStock());

        long refund = service.cancelOrder(order, now.plusHours(48));
        assertEquals(0L, refund);
        assertEquals(2, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.plusHours(49)));
        assertEquals(2, p.getStock());
    }
}
