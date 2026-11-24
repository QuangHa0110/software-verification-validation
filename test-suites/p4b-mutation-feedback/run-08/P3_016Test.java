package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void directOrderCopiesInputLinesAndSetStatusOnlyAssignsANonNullStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 7, 2, 4);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 58, 123_456_789);
        Order order = new Order(" O ", source, 3_000_000_014L, 3_000_000_014L, 17, 17, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();
        List<CartItem> snapshot = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(1, snapshot.get(0)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (OrderStatus status : new OrderStatus[]{
                OrderStatus.CANCELLED, OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.PAID}) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" O ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(3_000_000_014L, order.getDiscount());
            assertEquals(17L, order.getShipping());
            assertEquals(17L, order.getTotal());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(4, b.getStock());
        }
    }
}
