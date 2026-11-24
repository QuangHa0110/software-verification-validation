package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @Test
    void orderConstructorValidAndGetters() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = new Order("ORD-1", items, 20000L, 2000L, 15000L, 33000L, now, Region.INNER_CITY);
        assertEquals("ORD-1", order.getId());
        assertEquals(1, order.getItems().size());
        assertEquals(20000L, order.getSubtotal());
        assertEquals(2000L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(33000L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
    }
}
