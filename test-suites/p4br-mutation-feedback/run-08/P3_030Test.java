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
    void cancellationChecksNullTimeThenStatusThenTimeWithoutMutatingOrder(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1);
        Product a = new Product("A", "First", 10, 1, 5);
        Product b = new Product("B", "Second", 7, 2, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(status);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(status, order.getStatus());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());

        boolean terminal = status == OrderStatus.CANCELLED || status == OrderStatus.SHIPPED;
        Class<? extends RuntimeException> expected = terminal ? IllegalStateException.class : IllegalArgumentException.class;
        assertThrows(expected, () -> service.cancelOrder(order, t.minusNanos(1)));
        if (terminal) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        }
        assertEquals(status, order.getStatus());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(27L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(25_000L, order.getShipping());
        assertEquals(25_027L, order.getTotal());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
    }
}
