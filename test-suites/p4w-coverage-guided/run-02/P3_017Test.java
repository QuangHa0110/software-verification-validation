package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void cartUpdateQuantityScenarios() {
        Cart cart = new Cart();
        Product pA = new Product("A", "Món A", 1000L, 100, 10);
        cart.addItem(pA, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("B", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("   ", 5));

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", 51));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 11));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cart.updateQuantity("A", 10);
        assertEquals(10, cart.getItems().get(0).getQuantity());

        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());
    }
}
