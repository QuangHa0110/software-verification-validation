package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void failedCartMutationsPreserveEveryLineAndUseSpecifiedErrorPrecedence() {
        Cart cart = new Cart();
        Product a = new Product("A", "Original", 5, 8, 49);
        Product b = new Product("B", "Second", 7, 11, 2);
        Product replacement = new Product("A", "Replacement", 9, 1, 3);
        Product missing = new Product("C", "Missing", 1, 1, 0);
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
            assertEquals(49, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, missing.getStock());
        };
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int quantity : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            verify.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(missing, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
        verify.run();

        for (String id : new String[]{null, "", " \t", "a", " A ", "C"}) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            verify.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, quantity));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            verify.run();
        }
        cart.updateQuantity("B", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertEquals(1, b.getStock());
    }
}
