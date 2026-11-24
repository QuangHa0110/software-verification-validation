package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void cartUpdateQuantityExceptionsOrder() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 10);
        cart.addItem(p, 5);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", -1));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("UNKNOWN", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("UNKNOWN", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("UNKNOWN", 51));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));

        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 11));
        assertEquals(5, cart.getItems().get(0).getQuantity());

        p.reduceStock(7);
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }
}
