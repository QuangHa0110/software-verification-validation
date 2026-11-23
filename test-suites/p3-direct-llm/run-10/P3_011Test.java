package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void failedCartOperationsPreserveEveryLineAndCheckErrorsInOrder() {
        Product a = new Product(" A ", "A", 8, 10, 50);
        Product b = new Product("B", "B", 7, 20, 3);
        Product incoming = new Product(" A ", "Incoming", 90, 100, 3);
        Product unavailable = new Product("C", "C", 5, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(2);

        Runnable verify = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(49, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, incoming.getStock());
            assertEquals(0, unavailable.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int quantity : List.of(-1, 0)) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            verify.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(unavailable, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, Integer.MAX_VALUE));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
        verify.run();
        for (String missing : Arrays.asList(null, "", "\t ", "A", " A", " A  ", "b", "Z")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            verify.run();
            for (int quantity : List.of(0, 1, 51)) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, quantity));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            verify.run();
        }
    }
}
