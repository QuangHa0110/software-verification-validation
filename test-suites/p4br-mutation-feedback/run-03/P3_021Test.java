package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void directOrderSnapshotsSourceAndSetterOnlyAssignsNonNullStatus() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123_456_789);
        Product a = new Product("A", "First", 1_000_000_000, 1, 0);
        Product b = new Product("B", "Second", 17, 1, 0);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(b, 2), new CartItem(a, 3)));
        Order order = new Order(" D ", source, 3_000_000_034L, 3_000_000_034L, 13, 13, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> snapshot = order.getItems();
        source.clear();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, snapshot.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(snapshot.get(0)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED,
                                          OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(3_000_000_034L, order.getSubtotal());
            assertEquals(3_000_000_034L, order.getDiscount());
            assertEquals(13L, order.getShipping());
            assertEquals(13L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(b, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(a, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(0, b.getStock());
        }
    }
}
