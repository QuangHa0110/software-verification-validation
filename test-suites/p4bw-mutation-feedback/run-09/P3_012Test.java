package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartAddItemValidationAndErrors() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Name", 100L, 10, 50);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, -1));

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 51));

        Product lowStock = new Product("P2", "Low", 100L, 10, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(lowStock, 3));
        assertTrue(cart.isEmpty());

        cart.addItem(p, 49);
        Product pNew = new Product("P1", "Name", 100L, 10, 3);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pNew, 2));
        assertEquals(1, cart.getItems().size());
        assertEquals(49, cart.getItems().get(0).getQuantity());
    }
}
