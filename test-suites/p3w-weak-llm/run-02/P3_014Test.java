package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void cartAddItemValidationAndQuantityCap() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Sản phẩm", 1000L, 100, 100);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, -2));
        assertTrue(cart.isEmpty());

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 51));
        assertTrue(cart.isEmpty());

        cart.addItem(p, 49);
        assertEquals(1, cart.getItems().size());
        assertEquals(49, cart.getItems().get(0).getQuantity());

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());

        cart.addItem(p, 1);
        assertEquals(50, cart.getItems().get(0).getQuantity());
    }
}
