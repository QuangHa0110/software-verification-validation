package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void directOrderFreezesSourceLinesAndSetterOnlyAssignsStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 5, 0);
        Product b = new Product("B", "B", 7, 4, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 13, 123456789);
        Order order = new Order(" D ", source, 3_000_000_014L, 3_000_000_014L, 11, 11, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        source.clear();
        List<CartItem> frozen = order.getItems();
        assertEquals(2, frozen.size());
        assertSame(a, frozen.get(0).getProduct());
        assertEquals(3, frozen.get(0).getQuantity());
        assertSame(b, frozen.get(1).getProduct());
        assertEquals(2, frozen.get(1).getQuantity());
        Iterator<CartItem> iterator = frozen.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, frozen::clear);
        assertThrows(UnsupportedOperationException.class, () -> frozen.set(0, frozen.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> frozen.add(frozen.get(0)));

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING,
                OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
        }
        assertEquals(" D ", order.getId());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(3_000_000_014L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(11L, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(1, b.getStock());
    }
}
