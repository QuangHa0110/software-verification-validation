package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void orderSnapshotsSourceLinesAndAcceptsAnyNonNullStatusWithoutStockChanges() {
        Product a = new Product("A", "First", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "Second", 7, 1, 0);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 12, 987_654_321);
        Order order = new Order(" D ", source, 3_000_000_014L, 3_000_000_014L, 0, 0, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED,
                                          OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
        }
        List<CartItem> frozen = order.getItems();
        assertThrows(UnsupportedOperationException.class, frozen::clear);
        assertThrows(UnsupportedOperationException.class, () -> frozen.add(new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> frozen.set(0, new CartItem(b, 1)));
        Iterator<CartItem> iterator = frozen.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(2, frozen.size());
        assertSame(a, frozen.get(0).getProduct());
        assertEquals(3, frozen.get(0).getQuantity());
        assertSame(b, frozen.get(1).getProduct());
        assertEquals(2, frozen.get(1).getQuantity());
        assertEquals(" D ", order.getId());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(3_000_000_014L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(0L, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
    }
}
