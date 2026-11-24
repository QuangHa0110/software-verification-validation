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
    void cancellationValidatesNullTimeThenTerminalStatusThenCreationTime(OrderStatus status) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 42);
        Product a = new Product("A", "First", 7, 2, 3);
        Product b = new Product("B", "Second", 5, 1, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.REMOTE, time);
        order.setStatus(status);
        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(1, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(19L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(55_000L, order.getShipping());
            assertEquals(55_019L, order.getTotal());
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        }
        verify.run();
    }
}
