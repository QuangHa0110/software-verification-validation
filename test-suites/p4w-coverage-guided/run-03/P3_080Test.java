package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_080Test {

    @Test
    void orderItemsSnapshotImmutability() {
        Product p = new Product("P1", "Bút", 1000L, 10, 10);
        List<CartItem> originalList = new ArrayList<>();
        originalList.add(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("D1", originalList, 1000L, 0L, 0L, 1000L, now, Region.INNER_CITY);
        originalList.add(new CartItem(p, 2));

        assertEquals(1, order.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> order.getItems().remove(0));
    }
}
