package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_073Test {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "\t\n"})
    void orderInvalidId(String id) {
        Product p = new Product("P1", "Bút", 1000L, 10, 10);
        List<CartItem> items = List.of(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () ->
            new Order(id, items, 1000L, 0L, 0L, 1000L, now, Region.INNER_CITY));
    }
}
