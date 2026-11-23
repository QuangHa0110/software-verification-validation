package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void directOrderSnapshotsSourceAndSetterOnlyChangesStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 7, 2, 4);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987654321);
        long subtotal = 3_000_000_014L;
        Order order = new Order(" D ", source, subtotal, subtotal, 17, 17, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> snapshot = order.getItems();
        Collections.reverse(source);
        source.clear();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED,
                OrderStatus.PAID, OrderStatus.PENDING)) {
            order.setStatus(status);
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(subtotal, order.getSubtotal());
            assertEquals(subtotal, order.getDiscount());
            assertEquals(17L, order.getShipping());
            assertEquals(17L, order.getTotal());
            for (List<CartItem> items : List.of(snapshot, order.getItems())) {
                assertEquals(2, items.size());
                assertSame(a, items.get(0).getProduct());
                assertEquals(3, items.get(0).getQuantity());
                assertSame(b, items.get(1).getProduct());
                assertEquals(2, items.get(1).getQuantity());
            }
            assertEquals(0, a.getStock());
            assertEquals(4, b.getStock());
        }
    }
}
