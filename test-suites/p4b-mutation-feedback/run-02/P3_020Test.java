package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void directOrderFreezesSourceLinesAndSetterOnlyChangesStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 5, 1, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Order order = new Order(" D ", source, 3_000_000_010L, 3_000_000_010L, 7, 7, time, Region.REMOTE);
        assertEquals(2, source.size());
        source.clear();
        assertEquals(OrderStatus.PENDING, order.getStatus());

        for (OrderStatus status : new OrderStatus[]{OrderStatus.CANCELLED, OrderStatus.SHIPPED,
                OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.PAID}) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(3_000_000_010L, order.getSubtotal());
            assertEquals(3_000_000_010L, order.getDiscount());
            assertEquals(7L, order.getShipping());
            assertEquals(7L, order.getTotal());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }

        List<CartItem> snapshot = order.getItems();
        assertEquals(2, snapshot.size());
        assertSame(a, snapshot.get(0).getProduct());
        assertEquals(3, snapshot.get(0).getQuantity());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(2, snapshot.get(1).getQuantity());
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(a, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(2, order.getItems().size());
    }
}
