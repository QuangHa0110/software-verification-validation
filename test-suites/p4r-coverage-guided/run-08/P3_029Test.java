package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipAcceptOnlyTheirSpecifiedStartingStatuses(OrderStatus start) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        Product p = new Product("P", "Name", 103, 5, 4);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        for (boolean paying : new boolean[]{true, false}) {
            Order order = new Order("D", lines, 206, 6, 7, 207, t, Region.REMOTE);
            order.setStatus(start);
            if (paying && start == OrderStatus.PENDING) {
                service.pay(order);
                assertEquals(OrderStatus.PAID, order.getStatus());
            } else if (!paying && start == OrderStatus.PAID) {
                service.ship(order);
                assertEquals(OrderStatus.SHIPPED, order.getStatus());
            } else {
                assertThrows(IllegalStateException.class, () -> {
                    if (paying) {
                        service.pay(order);
                    } else {
                        service.ship(order);
                    }
                });
                assertEquals(start, order.getStatus());
            }
            assertEquals("D", order.getId());
            assertEquals(206L, order.getSubtotal());
            assertEquals(6L, order.getDiscount());
            assertEquals(7L, order.getShipping());
            assertEquals(207L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
            assertEquals(4, p.getStock());
        }
    }
}
