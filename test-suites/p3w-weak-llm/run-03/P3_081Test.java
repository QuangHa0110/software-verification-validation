package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_081Test {

    @Test
    void orderSetStatusSuccessAndNullCheck() {
        Product p = new Product("P1", "Bút", 1000L, 10, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D1", items, 1000L, 0L, 0L, 1000L, now, Region.INNER_CITY);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());

        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}
