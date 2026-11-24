package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_101Test {

    @Test
    void orderServiceCancelPendingOrderRestoresStockReturnsZeroRefund() {
        OrderService service = new OrderService();
        Product p = new Product("P01", "Book", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD1", cart, null, Region.INNER_CITY, now);

        assertEquals(3, p.getStock());
        long refund = service.cancelOrder(order, now.plusHours(48));

        assertEquals(0L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
    }
}
