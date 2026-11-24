package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_071Test {

    @Test
    void orderServiceCancelOrderPendingRestoresStockWithZeroRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 50000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, now);
        assertEquals(8, p.getStock());

        long refund = service.cancelOrder(order, now.plusHours(2));
        assertEquals(0L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(10, p.getStock());
    }
}
