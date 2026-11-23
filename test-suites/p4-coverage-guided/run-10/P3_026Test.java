package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void lifecycleRejectsNullOrdersAndOnlyAllowsPaymentThenShipment() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123);
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, t));

        Cart cart = new Cart();
        Product p = new Product("P", "P", 30, 100, 2);
        cart.addItem(p, 2);
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusHours(25)));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(0, p.getStock());
        assertEquals(60L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(25_000L, order.getShipping());
        assertEquals(25_060L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
