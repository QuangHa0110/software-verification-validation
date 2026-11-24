package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void orderConstructorValidAndGetters() {
        Product p = new Product("P2", "Book", 30000L, 500, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = List.of(item);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("D1", items, 60000L, 6000L, 15000L, 69000L, time, Region.INNER_CITY);
        assertEquals("D1", order.getId());
        assertEquals(1, order.getItems().size());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(60000L, order.getSubtotal());
        assertEquals(6000L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(69000L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().add(item));
    }
}
