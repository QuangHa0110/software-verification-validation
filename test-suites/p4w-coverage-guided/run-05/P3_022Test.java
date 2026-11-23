package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void testCartAddItemValidationRules() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 1000L, 10, 30);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, -1));

        Product lowStock = new Product("P2", "Notebook", 2000L, 20, 5);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(lowStock, 6));

        Product highStock = new Product("P3", "Pencil", 500L, 5, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(highStock, 51));

        cart.addItem(p, 30);
        Product pUpdated = new Product("P1", "Pen", 1000L, 10, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pUpdated, 21));
        assertEquals(30, cart.getItems().get(0).getQuantity());
    }
}
