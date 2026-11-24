package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void orderItemsImmutability() {
        Product p = new Product("P1", "Item", 30000L, 500, 10);
        List<CartItem> mutableList = new ArrayList<>();
        mutableList.add(new CartItem(p, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("D1", mutableList, 30000L, 0L, 0L, 30000L, now, Region.INNER_CITY);
        mutableList.add(new CartItem(p, 1));
        assertEquals(1, order.getItems().size());

        List<CartItem> orderItems = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> orderItems.add(new CartItem(p, 1)));
        assertThrows(UnsupportedOperationException.class, () -> orderItems.remove(0));
    }
}
