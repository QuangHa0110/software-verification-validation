package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void directOrderCopiesSourceListAndSetterAcceptsEveryNonNullStatus() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Product a = new Product("A", "First", 10, 7, 0);
        Product b = new Product("B", "Second", 7, 1, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(b, 1), new CartItem(a, 2)));
        Order order = new Order(" D ", source, 27, 27, 11, 11, time, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> snapshot = order.getItems();
        source.clear();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(a, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : List.of(OrderStatus.CANCELLED, OrderStatus.SHIPPED,
                                          OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(27L, order.getSubtotal());
            assertEquals(27L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(11L, order.getTotal());
            List<CartItem> rows = order.getItems();
            assertEquals(2, rows.size());
            assertSame(b, rows.get(0).getProduct());
            assertEquals(1, rows.get(0).getQuantity());
            assertSame(a, rows.get(1).getProduct());
            assertEquals(2, rows.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
