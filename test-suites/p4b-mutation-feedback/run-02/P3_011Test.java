package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void rejectedCartOperationsPreserveEveryLineAndRespectValidationPriority() {
        Cart cart = new Cart();
        Product a = new Product(" A ", "Original", 7, 2, 50);
        Product replacement = new Product(" A ", "Replacement", 9, 5, 3);
        Product b = new Product("B", "B", 11, 1, 2);
        Product absent = new Product("C", "C", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        a.reduceStock(2);

        Runnable verify = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(48, a.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(2, b.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, invalid));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        verify.run();

        for (String id : Arrays.asList(null, "", "\t ", "A", " a ", "missing")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            verify.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, quantity));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity(" A ", 49));
        verify.run();
        cart.updateQuantity(" A ", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        assertEquals(48, a.getStock());
    }
}
