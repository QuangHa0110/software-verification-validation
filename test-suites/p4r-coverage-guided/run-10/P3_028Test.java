package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationValidatesNullTimeThenStatusThenChronologyWithoutSideEffects(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 5);
        Product a = new Product("A", "first", 10, 2, 3);
        Product b = new Product("B", "second", 20, 1, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        if (status == OrderStatus.PAID || status == OrderStatus.SHIPPED) {
            service.pay(order);
        }
        if (status == OrderStatus.SHIPPED) {
            service.ship(order);
        }
        if (status == OrderStatus.CANCELLED) {
            assertEquals(0L, service.cancelOrder(order, t));
        }
        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(status == OrderStatus.CANCELLED ? 3 : 1, a.getStock());
            assertEquals(status == OrderStatus.CANCELLED ? 4 : 3, b.getStock());
            assertEquals(40L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_040L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(2, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(1, lines.get(1).getQuantity());
            }
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        boolean terminal = status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED;
        Class<? extends RuntimeException> expected = terminal ? IllegalStateException.class : IllegalArgumentException.class;
        assertThrows(expected, () -> service.cancelOrder(order, t.minusNanos(1)));
        verify.run();
        if (terminal) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(2)));
            verify.run();
        }
    }
}
