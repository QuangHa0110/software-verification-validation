package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationChecksNullTimeThenStatusThenChronologyWithoutPartialRestock(OrderStatus start) {
        Product a = new Product("A", "A", 20, 10, 3);
        Product b = new Product("B", "B", 7, 5, 5);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 3);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(start);
        Runnable check = () -> {
            assertEquals(start, order.getStatus());
            assertEquals(2, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(41L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_041L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            for (List<CartItem> rows : List.of(cart.getItems(), order.getItems())) {
                assertEquals(2, rows.size());
                assertSame(a, rows.get(0).getProduct());
                assertEquals(1, rows.get(0).getQuantity());
                assertSame(b, rows.get(1).getProduct());
                assertEquals(3, rows.get(1).getQuantity());
            }
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        check.run();
        if (start == OrderStatus.SHIPPED || start == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            check.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        check.run();
    }
}
