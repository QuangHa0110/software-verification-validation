package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_074Test {

    @Test
    void orderConstructorSubtotalMismatchThrowsException() {
        Product p = new Product("P01", "Book", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, 59999L, 0L, 15000L, 74999L, now, Region.INNER_CITY)
        );
    }
}
