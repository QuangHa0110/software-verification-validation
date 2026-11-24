package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void cartUpdateQuantityScenarios() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P1", 0));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P1", 51));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("", 5));

        Product p = new Product("P1", "Name", 100L, 10, 50);
        cart.addItem(p, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        Product low = new Product("P2", "Low", 50L, 10, 3);
        cart.addItem(low, 2);
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P2", 4));
        assertEquals(2, cart.getItems().get(1).getQuantity());

        cart.updateQuantity("P1", 50);
        assertEquals(50, cart.getItems().get(0).getQuantity());

        cart.updateQuantity("P1", 0);
        assertEquals(1, cart.getItems().size());
        assertEquals("P2", cart.getItems().get(0).getProduct().getId());
    }
}
