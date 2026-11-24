package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void failedCartWritesPreserveEveryLineAndRespectValidationPrecedence() {
        Product a = new Product("A", "First", 7, 2, 50);
        Product b = new Product("B", "Second", 9, 5, 2);
        Product incoming = new Product("A", "Replacement", 99, 1, 3);
        Product absent = new Product("C", "Absent", 1, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(1);

        Runnable verify = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(49, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(361L, cart.getSubtotal());
            assertEquals(108, cart.getTotalWeightGrams());
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, incoming.getStock());
            assertEquals(0, absent.getStock());
        };
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, invalid));
            verify.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        for (String missing : Arrays.asList(null, "", " \t", "a", " A ", "unknown")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            verify.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, quantity));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            verify.run();
        }
    }
}
