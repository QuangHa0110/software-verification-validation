package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationValidatesNullThenStateThenTimeAndLeavesAllStockIntact(OrderStatus status) {
        Product a = new Product("A", "A", 10, 1, 7);
        Product b = new Product("B", "B", 15, 2, 4);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, null, Region.SUBURB, t);
        order.setStatus(status);
        Runnable check = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(4, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(60L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_060L, order.getTotal());
        };

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        check.run();
        if (status == OrderStatus.CANCELLED || status == OrderStatus.SHIPPED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
            check.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusHours(24)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        }
        check.run();
    }
}
