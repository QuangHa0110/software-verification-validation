package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_049Test {

    @Test
    void testOrderItemsImmutability() {
        Product p = new Product("P1", "Pen", 100L, 10, 5);
        List<CartItem> mutableList = new ArrayList<>();
        mutableList.add(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("D1", mutableList, 100L, 0L, 0L, 100L, now, Region.INNER_CITY);
        mutableList.clear();
        assertEquals(1, order.getItems().size());

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().remove(0));
    }
}
