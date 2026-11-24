package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void directOrderFreezesSourceLinesAndSetterOnlyChangesStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 7, 2, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 58, 123_456_789);
        Order order = new Order(" O ", source, 3_000_000_014L, 3_000_000_014L, 17, 17, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> snapshot = order.getItems();
        Collections.reverse(source);
        source.clear();

        for (List<CartItem> lines : List.of(snapshot, order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertThrows(UnsupportedOperationException.class, lines::clear);
            assertThrows(UnsupportedOperationException.class, () -> lines.add(new CartItem(b, 1)));
            assertThrows(UnsupportedOperationException.class, () -> lines.set(0, lines.get(1)));
            ListIterator<CartItem> iterator = lines.listIterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }
        for (OrderStatus status : new OrderStatus[]{
                OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED, OrderStatus.PAID}) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" O ", order.getId());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(3_000_000_014L, order.getDiscount());
            assertEquals(17L, order.getShipping());
            assertEquals(17L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
