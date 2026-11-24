package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void directOrderFreezesSourceRowsAndAmountsAndAllowsAnyNonNullStatusAssignment() {
        Product a = new Product("A", "First", 1_000_000_000L, 10, 0);
        Product b = new Product("B", "Second", 7, 3, 1);
        CartItem first = new CartItem(a, 3);
        CartItem second = new CartItem(b, 2);
        List<CartItem> source = new ArrayList<>(List.of(first, second));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 1, 2, 345_678_901);
        Order order = new Order(" D ", source, 3_000_000_014L, 3_000_000_014L, 0, 0, t, Region.REMOTE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        Collections.reverse(source);
        source.clear();
        List<CartItem> rows = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> rows.add(first));
        assertThrows(UnsupportedOperationException.class, () -> rows.set(0, second));
        assertThrows(UnsupportedOperationException.class, () -> rows.remove(1));
        assertThrows(UnsupportedOperationException.class, rows::clear);
        ListIterator<CartItem> iterator = rows.listIterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> iterator.set(second));
        assertThrows(UnsupportedOperationException.class, () -> iterator.add(second));

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING,
                OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(" D ", order.getId());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(3_000_000_014L, order.getDiscount());
            assertEquals(0L, order.getShipping());
            assertEquals(0L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.REMOTE, order.getRegion());
            List<CartItem> saved = order.getItems();
            assertEquals(2, saved.size());
            assertSame(a, saved.get(0).getProduct());
            assertEquals(3, saved.get(0).getQuantity());
            assertSame(b, saved.get(1).getProduct());
            assertEquals(2, saved.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
    }
}
