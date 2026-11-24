package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationValidatesNullTimeThenStatusThenCreationTimeWithoutMutation(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 87);
        Product a = new Product("A", "First", 12, 2, 3);
        Product b = new Product("B", "Second", 13, 1, 7);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(status);

        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(1, a.getStock());
            assertEquals(4, b.getStock());
            assertEquals(63L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_063L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(2, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(3, lines.get(1).getQuantity());
            }
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(1)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        verify.run();
    }
}
