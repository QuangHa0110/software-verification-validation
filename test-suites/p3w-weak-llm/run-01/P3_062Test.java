package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_062Test {

    @Test
    void orderPackagePrivateConstructorValidation() {
        Product p = new Product("P1", "Prod 1", 10000L, 100, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 10, 0);

        // subtotal = 20000, discount = 5000, shipping = 15000, total = 30000
        Order order = new Order("ORD-1", items, 20000L, 5000L, 15000L, 30000L, now, Region.INNER_CITY);
        assertEquals("ORD-1", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(20000L, order.getSubtotal());
        assertEquals(5000L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(30000L, order.getTotal());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(1, order.getItems().size());
    }
}
