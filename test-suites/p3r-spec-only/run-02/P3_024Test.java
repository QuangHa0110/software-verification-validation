package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void orderCapturesSourceOrderAndQuantitiesAndExposesOnlyReadOnlyLines() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987654321);
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 7, 2, 1);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(b, 2), new CartItem(a, 3)));
        Order order = new Order(" D ", source, 3_000_000_014L, 14, 19, 3_000_000_019L,
            time, Region.REMOTE);
        List<CartItem> snapshot = order.getItems();
        Collections.reverse(source);
        source.clear();

        for (List<CartItem> lines : List.of(snapshot, order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(b, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(a, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
            assertThrows(UnsupportedOperationException.class, () -> lines.add(new CartItem(b, 1)));
            assertThrows(UnsupportedOperationException.class, () -> lines.set(0, new CartItem(a, 1)));
            assertThrows(UnsupportedOperationException.class, lines::clear);
            Iterator<CartItem> iterator = lines.iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }
        assertEquals(" D ", order.getId());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(14L, order.getDiscount());
        assertEquals(19L, order.getShipping());
        assertEquals(3_000_000_019L, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(1, b.getStock());
    }
}
