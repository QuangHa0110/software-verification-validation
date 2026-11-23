package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void rejectedCartChangesPreserveAllLinesAndRespectValidationOrder() {
        Cart cart = new Cart();
        Product first = new Product("A", "First", 10, 3, 50);
        Product second = new Product("B", "Second", 7, 2, 2);
        Product incoming = new Product("A", "Incoming", 90, 1, 3);
        Product absent = new Product("X", "Absent", 1, 1, 0);
        cart.addItem(first, 49);
        cart.addItem(second, 2);
        second.reduceStock(1);

        Runnable check = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(first, items.get(0).getProduct());
            assertEquals(49, items.get(0).getQuantity());
            assertSame(second, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(50, first.getStock());
            assertEquals(1, second.getStock());
            assertEquals(3, incoming.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        check.run();
        for (int qty : new int[] {-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(first, qty));
            check.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
        check.run();
        for (String id : Arrays.asList(null, "", " \t", "a", " A ", "X")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            check.run();
            for (int qty : new int[] {0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, qty));
                check.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            check.run();
        }
    }
}
