package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4_001Test {

    @Test
    void orderConstructorNullItemInListThrows() {
        List<CartItem> items = Collections.singletonList(null);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
                new Order("ORD1", items, 0, 0, 0, 0, now, Region.INNER_CITY));
    }
}
