package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void payAndShipPermitOnlyTheirSpecifiedTransitionsAndNeverChangeInventory() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("A", "A", 100, 2, 5);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(2, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(2, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(2, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, p.getStock());
        assertEquals(300L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(25_000L, order.getShipping());
        assertEquals(25_300L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(3, order.getItems().get(0).getQuantity());
    }
}
