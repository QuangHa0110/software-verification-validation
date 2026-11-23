package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void cartAddItemInsufficientStock() {
        Cart cart = new Cart();
        Product pLowStock = new Product("P1", "Low", 100L, 100, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(pLowStock, 3));
        assertTrue(cart.isEmpty());

        Product pValid = new Product("P2", "Valid", 100L, 100, 10);
        cart.addItem(pValid, 8);
        Product pUpdatedStock = new Product("P2", "Valid", 100L, 100, 8);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(pUpdatedStock, 1));
        assertEquals(8, cart.getItems().get(0).getQuantity());
    }
}
