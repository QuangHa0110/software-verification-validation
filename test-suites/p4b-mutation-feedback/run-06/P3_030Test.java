package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void paymentShipmentAndCancellationRejectNullAndRespectTerminalStates() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Product p = new Product("P", "P", 50, 1, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.REMOTE, time);

        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, time));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, p.getStock());

        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, p.getStock());

        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(0, p.getStock());
        assertEquals(100L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(55_000L, order.getShipping());
        assertEquals(55_100L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
