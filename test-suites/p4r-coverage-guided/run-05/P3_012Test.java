package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void rejectedCartAddsPreserveEveryLineAndUseLimitBeforeIncomingStock() {
        Cart cart = new Cart();
        Product a = new Product("A", "Original", 9, 1, 50);
        Product b = new Product("B", "Second", 4, 2, 2);
        Product low = new Product("A", "Replacement", 20, 3, 3);
        Product empty = new Product("C", "Empty", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        Runnable check = () -> {
            List<CartItem> rows = cart.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(49, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(2, rows.get(1).getQuantity());
            assertEquals(50, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(3, low.getStock());
            assertEquals(0, empty.getStock());
        };
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        check.run();
        for (int quantity : new int[] {-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            check.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(low, 2));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(empty, 51));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(low, 1));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(empty, 1));
        check.run();
    }
}
