package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void emptyCartsAreIndependentAndRemovingTheLastLineResetsTotals() {
        Cart cart = new Cart();
        List<CartItem> initial = cart.getItems();
        Product p = new Product("A", "A", 1_000_000_000L, 37, 3);
        cart.addItem(p, 1);
        cart.addItem(p, 2);
        Cart independent = new Cart();

        assertTrue(initial.isEmpty());
        assertTrue(independent.isEmpty());
        assertTrue(independent.getItems().isEmpty());
        assertEquals(0L, independent.getSubtotal());
        assertEquals(0, independent.getTotalWeightGrams());
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(111, cart.getTotalWeightGrams());

        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, p.getStock());
    }
}
