package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void orderItemsImmutabilityAndSnapshotIntegrity() {
        Product p = new Product("P1", "Sản phẩm", 30000L, 200, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> mutableList = new ArrayList<>();
        mutableList.add(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("D1", mutableList, 60000L, 0L, 15000L, 75000L, now, Region.INNER_CITY);
        mutableList.clear();

        assertEquals(1, order.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> order.getItems().remove(0));
    }
}
