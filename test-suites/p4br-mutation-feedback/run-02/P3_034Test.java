package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationChecksNullTimeThenStatusThenChronologyAndIsAtomicOnError(OrderStatus status) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Product a = new Product("A", "A", 10, 1, 3);
        Product b = new Product("B", "B", 20, 2, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
        order.setStatus(status);
        boolean terminal = status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED;
        List<LocalDateTime> times = new ArrayList<>(Arrays.asList(null, time.minusNanos(1)));
        if (terminal) {
            times.add(time);
            times.add(time.plusDays(2));
        }
        for (LocalDateTime attempt : times) {
            Class<? extends RuntimeException> expected = attempt != null && terminal
                ? IllegalStateException.class : IllegalArgumentException.class;
            assertThrows(expected, () -> service.cancelOrder(order, attempt));
            assertEquals(status, order.getStatus());
            assertEquals(1, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(time, order.getCreatedAt());
            assertEquals(80L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25000L, order.getShipping());
            assertEquals(25080L, order.getTotal());
            for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
                assertEquals(2, lines.size());
                assertSame(a, lines.get(0).getProduct());
                assertEquals(2, lines.get(0).getQuantity());
                assertSame(b, lines.get(1).getProduct());
                assertEquals(3, lines.get(1).getQuantity());
            }
        }
    }
}
