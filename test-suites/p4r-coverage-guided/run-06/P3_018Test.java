package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void directOrderFreezesInputLinesAndAllowsAnyNonNullStatusAssignment() {
        Product a = new Product("A", "A", 1_000_000_000L, 1, 0);
        Product b = new Product("B", "B", 7, 1, 0);
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(a, 3), new CartItem(b, 2)));
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 7, 123_456_789);
        long subtotal = 3_000_000_014L;
        Order order = new Order(" D ", source, subtotal, subtotal, 11, 11, t, Region.PROVINCE);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        List<CartItem> before = order.getItems();
        source.clear();

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING,
                                          OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
        }
        for (List<CartItem> snapshot : List.of(before, order.getItems())) {
            assertEquals(2, snapshot.size());
            assertSame(a, snapshot.get(0).getProduct());
            assertEquals(3, snapshot.get(0).getQuantity());
            assertSame(b, snapshot.get(1).getProduct());
            assertEquals(2, snapshot.get(1).getQuantity());
            assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(a, 1)));
            assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(b, 1)));
            assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
            Iterator<CartItem> iterator = snapshot.iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }
        assertEquals(" D ", order.getId());
        assertEquals(subtotal, order.getSubtotal());
        assertEquals(subtotal, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(11L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
    }
}
