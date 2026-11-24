package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipEnforceTheirOwnTransitionsWithoutChangingOtherOrderData(OrderStatus initial) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 17);
        Product p = new Product("P", "P", 35, 1, 5);
        Order order = new Order("D", List.of(new CartItem(p, 2)), 70, 3, 11, 78, t, Region.REMOTE);
        OrderService service = new OrderService();
        for (boolean paying : new boolean[]{true, false}) {
            order.setStatus(initial);
            if (paying && initial == OrderStatus.PENDING) {
                service.pay(order);
                assertEquals(OrderStatus.PAID, order.getStatus());
            } else if (!paying && initial == OrderStatus.PAID) {
                service.ship(order);
                assertEquals(OrderStatus.SHIPPED, order.getStatus());
            } else {
                if (paying) {
                    assertThrows(IllegalStateException.class, () -> service.pay(order));
                } else {
                    assertThrows(IllegalStateException.class, () -> service.ship(order));
                }
                assertEquals(initial, order.getStatus());
            }
            assertEquals(5, p.getStock());
            assertEquals("D", order.getId());
            assertEquals(70L, order.getSubtotal());
            assertEquals(3L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(78L, order.getTotal());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(t, order.getCreatedAt());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
