package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartAddFailuresPreserveEveryLineAndCheckLimitBeforeIncomingStock() {
        Cart cart = new Cart();
        Product a = new Product("A", "old", 9, 1, 50);
        Product b = new Product("B", "B", 2, 3, 5);
        Product replacement = new Product("A", "replacement", 12, 7, 3);
        Product missing = new Product("C", "C", 10, 4, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        Runnable checkCart = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(49, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(50, a.getStock());
            assertEquals(5, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, missing.getStock());
        };
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        checkCart.run();
        for (int qty : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(b, qty));
            checkCart.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(missing, 1));
        checkCart.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(missing, 51));
        checkCart.run();
    }
}
