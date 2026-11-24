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
    void cancellationChecksNullTimeThenStatusThenCreationTimeWithoutSideEffects(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1);
        Product a = new Product("A", "A", 10, 1, 2);
        Product b = new Product("B", "B", 20, 1, 5);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.PROVINCE, t);
        order.setStatus(status);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(status, order.getStatus());
        assertEquals(0, a.getStock());
        assertEquals(2, b.getStock());
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        assertEquals(status, order.getStatus());
        assertEquals(0, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(80L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(35_080L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
        }
    }
}
