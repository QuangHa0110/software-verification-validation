package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void rejectedCartOperationsPreserveEveryLineAndApplyChecksInOrder() {
        Cart cart = new Cart();
        Product a = new Product("A", "first", 9, 4, 49);
        Product b = new Product("B", "second", 11, 5, 2);
        Product incoming = new Product("A", "replacement", 20, 7, 3);
        Product absent = new Product("C", "absent", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(1);
        Runnable verify = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(49, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, incoming.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int quantity : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
        verify.run();
        for (String id : Arrays.asList(null, "", " \t", "a", " A ", "missing")) {
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
