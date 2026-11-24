package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void cartUpdateQuantityExceedsLimitOrStock() {
        Cart cart = new Cart();
        Product p = new Product("A", "Product A", 100L, 10, 3);
        cart.addItem(p, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", 51));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 4));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cart.updateQuantity("A", 3);
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }
}
