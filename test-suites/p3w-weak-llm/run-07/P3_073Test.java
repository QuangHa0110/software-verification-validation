package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_073Test {

    @Test
    void orderConstructorListContainsNullItemThrowsException() {
        List<CartItem> items = new ArrayList<>();
        items.add(null);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, 0L, 0L, 0L, 0L, now, Region.INNER_CITY)
        );
    }
}
