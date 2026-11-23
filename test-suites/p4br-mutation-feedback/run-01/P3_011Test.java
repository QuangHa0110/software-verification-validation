package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartUpdateAndRemoveFailuresRespectLookupOrderAndRecheckCurrentStock() {
        Cart cart = new Cart();
        Product a = new Product(" A ", "A", 7, 2, 3);
        Product b = new Product("B", "B", 11, 5, 2);
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        a.reduceStock(2);
        Runnable checkCart = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(2, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(1, items.get(1).getQuantity());
            assertEquals(1, a.getStock());
            assertEquals(2, b.getStock());
        };
        for (String absent : Arrays.asList(null, "", "\t ", "A", " a ", "missing")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(absent, -1));
            checkCart.run();
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(absent, quantity));
                checkCart.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(absent));
            checkCart.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", -1));
        checkCart.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", 51));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity(" A ", 2));
        checkCart.run();
        cart.updateQuantity(new String(" A "), 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertSame(b, cart.getItems().get(1).getProduct());
    }
}
