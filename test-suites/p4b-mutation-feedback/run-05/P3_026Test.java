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
    void failedCancellationPreservesAllLinesAmountsStocksAndOriginalStatus(OrderStatus status) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 9);
        Product a = new Product("A", "First", 20, 1, 4);
        Product b = new Product("B", "Second", 13, 2, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
        order.setStatus(status);
        Runnable verify = () -> {
            assertEquals(status, order.getStatus());
            assertEquals(2, a.getStock());
            assertEquals(0, b.getStock());
            assertEquals(53L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_053L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        };
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        verify.run();
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.plusDays(2)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        }
        verify.run();
    }
}
