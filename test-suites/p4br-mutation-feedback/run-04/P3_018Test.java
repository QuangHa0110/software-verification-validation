package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void orderCopiesInputLinesAndStatusSetterOnlyWritesTheStatus() {
        Product a = new Product("A", "A", 1_000_000_000L, 5, 0);
        Product b = new Product("B", "B", 7, 2, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 7, 123456789);
        Order order = new Order(" D ", source, 3_000_000_014L, 3_000_000_014L, 0, 0, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> snapshot = order.getItems();
        source.clear();
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(1, new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(b, 1)));

        for (OrderStatus status : new OrderStatus[]{OrderStatus.SHIPPED, OrderStatus.PENDING,
                                                    OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAID}) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(3_000_000_014L, order.getDiscount());
            assertEquals(0L, order.getShipping());
            assertEquals(0L, order.getTotal());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
