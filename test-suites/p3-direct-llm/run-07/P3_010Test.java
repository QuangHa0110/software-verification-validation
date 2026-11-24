package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartErrorsRespectValidationOrderAndPreserveEveryExistingLine() {
        Product a = new Product("A", "Original", 9, 2, 50);
        Product b = new Product("B", "Second", 7, 3, 2);
        Product replacement = new Product("A", "Replacement", 12, 4, 3);
        Product absent = new Product("C", "Absent", 1, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(1);

        Runnable checkCart = () -> {
            List<CartItem> items = cart.getItems();
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(49, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(2, items.get(1).getQuantity());
            assertEquals(455L, cart.getSubtotal());
            assertEquals(104, cart.getTotalWeightGrams());
            assertEquals(50, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(3, replacement.getStock());
            assertEquals(0, absent.getStock());
        };

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        checkCart.run();
        for (int qty : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, qty));
            checkCart.run();
        }
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        checkCart.run();
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
        checkCart.run();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        checkCart.run();
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
        checkCart.run();
        for (String id : Arrays.asList(null, "", " \t", "a", " A ", "C")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            checkCart.run();
            for (int qty : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, qty));
                checkCart.run();
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            checkCart.run();
        }
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        b.reduceStock(1);
        cart.updateQuantity("B", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertEquals(0, b.getStock());
    }
}
