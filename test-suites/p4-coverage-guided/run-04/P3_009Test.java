package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartRejectsInvalidChangesWithoutReplacingReorderingOrReservingItems() {
        Cart cart = new Cart();
        Product a = new Product(" A ", "First", 2, 3, 50);
        Product b = new Product("B", "Second", 3, 5, 2);
        Product replacement = new Product(" A ", "Other", 7, 9, 3);
        Product unavailable = new Product("C", "Third", 10, 1, 0);
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
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, unavailable.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int quantity : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            verify.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(unavailable, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        for (String absent : Arrays.asList(null, "", "\t ", "A", " a ", "unknown")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(absent, -1));
            verify.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(absent, quantity));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(absent));
            verify.run();
        }
    }
}
