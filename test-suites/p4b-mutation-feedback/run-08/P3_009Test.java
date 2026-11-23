package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void rejectedCartChangesPreserveEveryLineAndRespectValidationOrder() {
        Product a = new Product(" A ", "First", 9, 2, 50);
        Product b = new Product("B", "Second", 7, 3, 2);
        Product incoming = new Product(" A ", "Replacement", 20, 8, 3);
        Cart cart = new Cart();
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
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, incoming.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        verify.run();
        for (int qty : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, qty));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        verify.run();
        assertThrows(InsufficientStockException.class,
            () -> cart.addItem(new Product("C", "Third", 1, 1, 2), 3));
        verify.run();
        for (String missing : new String[]{null, "", " \t", "A", "b", "missing"}) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1));
            verify.run();
            for (int qty : new int[]{0, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, qty));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        verify.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        verify.run();
    }
}
