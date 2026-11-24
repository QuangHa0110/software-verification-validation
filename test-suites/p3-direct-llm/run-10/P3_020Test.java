package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void directOrderTakesAnImmutableSnapshotAndSetterOnlyAssignsStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 3, 0);
        Product b = new Product("B", "B", 7, 4, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 1)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 58, 123456789);
        Order order = new Order(" D ", source, 3_000_000_007L, 3_000_000_007L,
            11, 11, t, Region.PROVINCE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();
        List<CartItem> snapshot = order.getItems();
        assertEquals(2, snapshot.size());
        assertSame(a, snapshot.get(0).getProduct());
        assertEquals(3, snapshot.get(0).getQuantity());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(1, snapshot.get(1).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, snapshot.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PAID, OrderStatus.CANCELLED,
                OrderStatus.CANCELLED, OrderStatus.PENDING)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(3_000_000_007L, order.getSubtotal());
            assertEquals(3_000_000_007L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(11L, order.getTotal());
            assertEquals(List.of(a, b), order.getItems().stream().map(CartItem::getProduct).toList());
            assertEquals(List.of(3, 1), order.getItems().stream().map(CartItem::getQuantity).toList());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
