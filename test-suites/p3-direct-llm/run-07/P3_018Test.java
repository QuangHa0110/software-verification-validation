package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void orderFreezesSourceLinesAndSetterAcceptsEveryNonNullStatusWithoutStockChanges() {
        Product a = new Product("A", "First", 1_000_000_000L, 11, 0);
        Product b = new Product("B", "Second", 5, 13, 0);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987654321);
        Order order = new Order(" D ", source, 3_000_000_010L, 3_000_000_010L, 0, 0, time, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();
        List<CartItem> snapshot = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(1, new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(b, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : new OrderStatus[]{
                OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAID}) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(3_000_000_010L, order.getSubtotal());
            assertEquals(3_000_000_010L, order.getDiscount());
            assertEquals(0L, order.getShipping());
            assertEquals(0L, order.getTotal());
            List<CartItem> items = order.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(3, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(0, b.getStock());
        }
    }
}
