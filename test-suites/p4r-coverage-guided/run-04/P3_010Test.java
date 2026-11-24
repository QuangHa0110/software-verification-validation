package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void rejectedCartWritesPreserveEveryLineAndRespectValidationPrecedence() {
        Product a = new Product(" A ", "A", 9, 10, 50);
        Product b = new Product("B", "B", 12, 20, 4);
        Product poor = new Product(new String(" A "), "Other A", 99, 99, 3);
        Product absent = new Product("C", "C", 1, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(3);

        Runnable verify = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(465L, cart.getSubtotal());
            assertEquals(530, cart.getTotalWeightGrams());
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, poor.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int qty : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(b, qty));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(poor, 2));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(poor, 1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", -1));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
        for (String missing : Arrays.asList(null, "", " \t", "A", " a ", "unknown")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            verify.run();
            for (int qty : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, qty));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            verify.run();
        }
    }
}
