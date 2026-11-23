package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationValidatesNullTimeThenTerminalStatusThenCreationTime(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123_456_789);
        Product a = new Product("A", "A", 8, 10, 3);
        Product b = new Product("B", "B", 9, 20, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, null, Region.INNER_CITY, t);
        order.setStatus(status);
        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(1, a.getStock());
            assertEquals(0, b.getStock());
            assertEquals(25L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(15_025L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(List.of(a, b), order.getItems().stream().map(CartItem::getProduct).toList());
            assertEquals(List.of(2, 1), order.getItems().stream().map(CartItem::getQuantity).toList());
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
