package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void cartAddItemLimitAndStockChecks() {
        Cart cart = new Cart();
        Product pLowStock = new Product("P1", "Name", 1000L, 10, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(pLowStock, 3));
        assertTrue(cart.isEmpty());

        Product pHighStock = new Product("P2", "Name", 1000L, 10, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pHighStock, 51));
        assertTrue(cart.isEmpty());

        cart.addItem(pHighStock, 49);
        assertEquals(1, cart.getItems().size());
        assertEquals(49, cart.getItems().get(0).getQuantity());

        Product pUpdated = new Product("P2", "New Name", 1200L, 15, 3);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pUpdated, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());
        assertEquals(1000L, cart.getItems().get(0).getProduct().getPrice());
    }
}
