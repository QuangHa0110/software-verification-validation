package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void directOrderFreezesSourceLinesAndAcceptsFullDiscountWithoutCheckingStock() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 5, 1, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(b, 2), new CartItem(a, 3)));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 58, 987654321);
        long subtotal = 3_000_000_010L;
        Order order = new Order(" D ", source, subtotal, subtotal, 13, 13, time, Region.REMOTE);
        source.clear();

        List<CartItem> lines = order.getItems();
        assertEquals(2, lines.size());
        assertSame(b, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(a, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
        Iterator<CartItem> iterator = lines.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, lines::clear);
        assertThrows(UnsupportedOperationException.class, () -> lines.set(0, new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> lines.add(new CartItem(b, 1)));
        assertEquals(" D ", order.getId());
        assertEquals(subtotal, order.getSubtotal());
        assertEquals(subtotal, order.getDiscount());
        assertEquals(13L, order.getShipping());
        assertEquals(13L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, a.getStock());
        assertEquals(1, b.getStock());
    }
}
