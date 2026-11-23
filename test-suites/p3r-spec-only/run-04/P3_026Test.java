package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationChecksNullTimeThenTerminalStateThenCreationTime(OrderStatus status) {
        Product a = new Product("A", "A", 10, 1, 3);
        Product b = new Product("B", "B", 20, 1, 3);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 321);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(status);
        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(2, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(50L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25000L, order.getShipping());
            assertEquals(25050L, order.getTotal());
            for (List<CartItem> lines : List.of(order.getItems(), cart.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(1, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(2, lines.get(1).getQuantity());
            }
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        verify.run();
    }
}
