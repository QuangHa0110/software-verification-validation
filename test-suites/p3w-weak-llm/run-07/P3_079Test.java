package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_079Test {

    @Test
    void orderSetStatusNullThrowsException() {
        Product p = new Product("P01", "Book", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("ORD1", items, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY);
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }
}
