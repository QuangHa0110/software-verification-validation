package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartsAreIndependentAndAggregateMoneyInLongAndWeightInGrams() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7_000, 150, 2);
        cart.addItem(a, 3);
        cart.addItem(b, 1);
        cart.addItem(b, 1);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_014_000L, cart.getSubtotal());
        assertEquals(900, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());

        Cart other = new Cart();
        assertTrue(other.isEmpty());
        assertTrue(other.getItems().isEmpty());
        cart.removeItem("A");
        assertEquals(14_000L, cart.getSubtotal());
        cart.updateQuantity("B", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());
    }
}
