package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartLookupAndQuantityFailuresRespectPrecedenceAndPreserveEveryLine() {
        Cart cart = new Cart();
        Product a = new Product(" A ", "First", 7, 2, 5);
        Product b = new Product("B", "Second", 8, 3, 2);
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        a.reduceStock(3);

        Runnable verify = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(2, a.getStock());
            assertEquals(2, b.getStock());
        };
        for (String id : Arrays.asList(null, "", " \t", "A", " a ", "missing")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            verify.run();
            for (int qty : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, qty));
                verify.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            verify.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", -1));
        verify.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", 51));
        verify.run();
        for (int qty : new int[]{3, 4, 50}) {
            assertThrows(InsufficientStockException.class, () -> cart.updateQuantity(" A ", qty));
            verify.run();
        }
        cart.updateQuantity(new String(" A "), 0);
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(2, a.getStock());
    }
}
