package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4_001Test {

    @Test
    void testCancelPendingOrderRefundsZeroAndRestoresStock() {
        Product product = new Product("P1", "Pen", 10_000, 100, 10);
        Cart cart = new Cart();
        cart.addItem(product, 2);

        OrderService service = new OrderService();
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 23, 8, 0, 0);
        Order order = service.placeOrder("ORD-001", cart, null, Region.INNER_CITY, createdAt);

        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
        Assertions.assertEquals(8, product.getStock());

        LocalDateTime cancelTime = createdAt.plusHours(2);
        long refund = service.cancelOrder(order, cancelTime);

        Assertions.assertEquals(0L, refund);
        Assertions.assertEquals(OrderStatus.CANCELLED, order.getStatus());
        Assertions.assertEquals(10, product.getStock());
    }
}
