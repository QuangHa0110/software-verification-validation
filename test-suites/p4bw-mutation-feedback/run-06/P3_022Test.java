package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void cartAddItemQuantityLimitsAndStockCheck() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 10);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(p, 11));
        assertTrue(cart.isEmpty());

        Product largeStock = new Product("P2", "Book", 10000L, 100, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(largeStock, 51));
        assertTrue(cart.isEmpty());

        cart.addItem(largeStock, 49);
        assertEquals(49, cart.getItems().get(0).getQuantity());

        Product smallStock = new Product("P2", "Book", 10000L, 100, 3);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(smallStock, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());

        Product validStock = new Product("P2", "Book", 10000L, 100, 100);
        cart.addItem(validStock, 1);
        assertEquals(50, cart.getItems().get(0).getQuantity());
    }
}
