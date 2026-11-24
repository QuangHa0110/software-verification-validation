package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @Test
    void testCartUpdateQuantityOrderOfValidation() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P1", 0));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P1", 51));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));

        Product p = new Product("P1", "Pen", 100L, 10, 3);
        cart.addItem(p, 2);
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 4));
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
