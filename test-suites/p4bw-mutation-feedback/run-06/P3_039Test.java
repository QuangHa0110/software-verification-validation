package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void orderSetStatusAndImmutability() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> originalList = new ArrayList<>();
        originalList.add(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = new Order("O1", originalList, 10000L, 0L, 15000L, 25000L, now, Region.INNER_CITY);
        originalList.add(new CartItem(p, 2));
        assertEquals(1, order.getItems().size());

        List<CartItem> orderItems = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> orderItems.add(new CartItem(p, 1)));

        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }
}
