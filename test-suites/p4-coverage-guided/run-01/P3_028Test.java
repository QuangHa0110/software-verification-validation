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
    void cancellationChecksNullTimeThenStatusThenChronologyWithoutMutatingOrder(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 987654321);
        Product a = new Product("A", "A", 40, 5, 3);
        Product b = new Product("B", "B", 7, 2, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.REMOTE, t);
        order.setStatus(status);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(status, order.getStatus());
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        assertEquals(status, order.getStatus());
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());
        assertEquals(87L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(55_000L, order.getShipping());
        assertEquals(55_087L, order.getTotal());
        assertEquals(List.of(a, b), order.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 1), order.getItems().stream().map(CartItem::getQuantity).toList());
    }
}
