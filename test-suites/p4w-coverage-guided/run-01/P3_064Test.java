package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_064Test {

    @Test
    void orderSetStatusValidAndNull() {
        Product p = new Product("P1", "Prod 1", 10000L, 100, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 10, 0);
        Order order = new Order("O1", items, 10000L, 0L, 15000L, 25000L, now, Region.INNER_CITY);

        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }
}
