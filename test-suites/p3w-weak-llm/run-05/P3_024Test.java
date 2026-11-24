package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void testCartUpdateQuantityInvalidArguments() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 1000L, 10, 20);
        cart.addItem(p, 5);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P2", 2));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 2));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("   ", 2));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 25));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }
}
