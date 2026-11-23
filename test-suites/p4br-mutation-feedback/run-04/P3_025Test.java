package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipEnforceTheirOwnSourceStatesAndPreserveOrderData(OrderStatus initial) {
        Product p = new Product("P", "P", 19, 1, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.REMOTE, t);
        Runnable verify = () -> {
            assertEquals(0, p.getStock());
            assertEquals("D", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(38L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(55000L, order.getShipping());
            assertEquals(55038L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        };
        order.setStatus(initial);
        if (initial == OrderStatus.PENDING) {
            service.pay(order);
            assertEquals(OrderStatus.PAID, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.pay(order));
            assertEquals(initial, order.getStatus());
        }
        verify.run();
        order.setStatus(initial);
        if (initial == OrderStatus.PAID) {
            service.ship(order);
            assertEquals(OrderStatus.SHIPPED, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.ship(order));
            assertEquals(initial, order.getStatus());
        }
        verify.run();
    }
}
