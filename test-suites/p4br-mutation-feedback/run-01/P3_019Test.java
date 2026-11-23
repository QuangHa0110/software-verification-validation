package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void directOrderFreezesSourceLinesAndAllowsAnyNonNullStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 13, 2, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 17, 42, 13, 987654321);
        Order order = new Order(" D ", source, 3_000_000_026L, 3_000_000_026L, 0, 0, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> saved = order.getItems();
        source.clear();
        assertThrows(UnsupportedOperationException.class, () -> saved.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> saved.add(new CartItem(b, 1)));
        Iterator<CartItem> iterator = saved.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, saved::clear);
        for (List<CartItem> lines : List.of(saved, order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
        }
        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED,
                OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(3_000_000_026L, order.getSubtotal());
            assertEquals(3_000_000_026L, order.getDiscount());
            assertEquals(0L, order.getShipping());
            assertEquals(0L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
