package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void orderSnapshotsInputAndDirectStatusSetterOnlyChangesStatus() {
        Product first = new Product("A", "First", 1_000_000_000L, 3, 0);
        Product second = new Product("B", "Second", 5, 1, 2);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(first, 3), new CartItem(second, 2)));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Order order = new Order(" D ", source, 3_000_000_010L, 3_000_000_010L, 11, 11, time, Region.PROVINCE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();
        List<CartItem> snapshot = order.getItems();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(second, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(first, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED, OrderStatus.PAID)) {
            order.setStatus(status);
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(3_000_000_010L, order.getSubtotal());
            assertEquals(3_000_000_010L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(11L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            List<CartItem> items = order.getItems();
            assertEquals(2, items.size());
            assertSame(first, items.get(0).getProduct());
            assertEquals(3, items.get(0).getQuantity());
            assertSame(second, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(0, first.getStock());
            assertEquals(2, second.getStock());
        }
    }
}
