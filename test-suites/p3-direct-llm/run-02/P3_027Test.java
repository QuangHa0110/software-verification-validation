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
    void cancellationValidatesNullsThenStatusThenTimeWithoutSideEffects(OrderStatus status) {
        OrderService service = new OrderService();
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 7);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, time));
        Product a = new Product("A", "A", 10, 2, 4);
        Product b = new Product("B", "B", 7, 3, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, time);
        order.setStatus(status);

        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(2, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(27L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(15_027L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
            for (List<CartItem> lines : List.of(order.getItems(), cart.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(2, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(1, lines.get(1).getQuantity());
            }
        };

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        LocalDateTime early = time.minusNanos(1);
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, early));
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, early));
        }
        verify.run();
    }
}
