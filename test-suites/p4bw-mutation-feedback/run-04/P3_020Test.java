package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void cartUpdateQuantityExceptionsAndOrder() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Item", 100L, 100, 3);
        cart.addItem(p, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", -1));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("NON_EXISTENT", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("NON_EXISTENT", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("NON_EXISTENT", 0));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("NON_EXISTENT", 51));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 4));
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
