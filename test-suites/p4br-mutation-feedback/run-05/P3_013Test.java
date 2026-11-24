package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void cartUpdatesAndRemovalsCheckArgumentsInOrderAndRecheckCurrentStock() {
        Cart cart = new Cart();
        Product a = new Product(" A ", "First", 7, 3, 50);
        Product b = new Product("B", "Second", 11, 5, 2);
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        cart.updateQuantity(" A ", 50);
        a.reduceStock(1);
        Runnable check = () -> {
            List<CartItem> rows = cart.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(50, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(1, rows.get(1).getQuantity());
            assertEquals(49, a.getStock());
            assertEquals(2, b.getStock());
        };
        for (String missing : Arrays.asList(null, "", " \t", "A", " a ", "other")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            check.run();
            for (int qty : new int[] {0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, qty));
                check.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            check.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", -1));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", 51));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity(" A ", 50));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
        check.run();
        cart.updateQuantity(" A ", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(49, a.getStock());
    }
}
