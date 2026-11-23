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
    void cancellationChecksNullTimeThenStatusThenCreationTimeAndPreservesBothStocks(OrderStatus initial) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1234);
        Product a = new Product("A", "A", 20, 2, 4);
        Product b = new Product("B", "B", 3, 1, 7);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(initial);
        Runnable checkState = () -> {
            assertEquals(initial, order.getStatus());
            assertEquals(2, a.getStock());
            assertEquals(4, b.getStock());
            assertEquals(49L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25000L, order.getShipping());
            assertEquals(25049L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            for (List<CartItem> lines : List.of(order.getItems(), cart.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(2, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(3, lines.get(1).getQuantity());
            }
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        checkState.run();
        if (initial == OrderStatus.SHIPPED || initial == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            checkState.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(2)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        checkState.run();
    }
}
