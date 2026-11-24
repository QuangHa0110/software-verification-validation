package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void rejectedCartWritesPreserveEveryLineAndCheckQuantityLimitBeforeStock() {
        Product a = new Product("A", "A", 9, 2, 50);
        Product b = new Product("B", "B", 7, 3, 2);
        Product replacement = new Product("A", "Other A", 20, 5, 3);
        Product absent = new Product("C", "C", 1, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(1);

        Runnable check = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        check.run();
        for (int quantity : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
            check.run();
        }
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        check.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        check.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        check.run();
        for (String missing : Arrays.asList(null, "", " \t", "a", " A ", "C")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            check.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, quantity));
                check.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            check.run();
        }
    }
}
