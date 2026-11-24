package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void lifecycleRejectsNullOrdersAndAllowsOnlyPendingPaymentAndPaidShipment() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, t));

        Product p = new Product("A", "A", 100, 10, 3);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(1)));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(1, p.getStock());
        assertEquals(25_200L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
