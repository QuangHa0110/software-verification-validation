package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void paymentAndShipmentEnforceTheirSourceStatesAndOnlyChangeStatus(OrderStatus initial) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 10, 1, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.REMOTE, time);
        order.setStatus(initial);
        if (initial == OrderStatus.PENDING) {
            service.pay(order);
            assertEquals(OrderStatus.PAID, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.pay(order));
            assertEquals(initial, order.getStatus());
        }
        assertEquals(0, p.getStock());
        order.setStatus(initial);
        if (initial == OrderStatus.PAID) {
            service.ship(order);
            assertEquals(OrderStatus.SHIPPED, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.ship(order));
            assertEquals(initial, order.getStatus());
        }
        assertEquals(0, p.getStock());
        assertEquals(20L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(55000L, order.getShipping());
        assertEquals(55020L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
