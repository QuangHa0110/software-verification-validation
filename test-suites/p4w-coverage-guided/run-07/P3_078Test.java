package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_078Test {

    @Test
    void orderConstructorNullDateOrRegionThrowsException() {
        Product p = new Product("P01", "Book", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, 60000L, 0L, 15000L, 75000L, null, Region.INNER_CITY)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, 60000L, 0L, 15000L, 75000L, now, null)
        );
    }
}
