package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void rejectedAddsPreserveEveryLineAndCheckCombinedLimitBeforeIncomingStock() {
        Product a = new Product("A", "Original", 7, 2, 50);
        Product b = new Product("B", "Second", 8, 3, 4);
        Product replacement = new Product("A", "Replacement", 19, 5, 3);
        Product absent = new Product("C", "Absent", 1, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);

        Runnable verify = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(50, a.getStock());
            assertEquals(4, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, absent.getStock());
        };
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int qty : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, qty));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
        verify.run();
    }
}
