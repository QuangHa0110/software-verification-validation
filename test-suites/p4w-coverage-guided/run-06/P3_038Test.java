package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void orderConstructorInvalidDateOrRegion() {
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 10000L, 0L, 15000L, 25000L, null, Region.INNER_CITY));
        assertThrows(IllegalArgumentException.class, () -> new Order("O1", items, 10000L, 0L, 15000L, 25000L, now, null));
    }
}
