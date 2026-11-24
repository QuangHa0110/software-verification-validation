package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void independentCartsAccumulateLongSubtotalsAndReturnToEmpty() {
        Cart cart = new Cart();
        Cart other = new Cart();
        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7_000, 150, 2);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        List<CartItem> initiallyEmpty = cart.getItems();

        cart.addItem(a, 1);
        cart.addItem(a, 2);
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(600, cart.getTotalWeightGrams());
        cart.addItem(b, 2);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_014_000L, cart.getSubtotal());
        assertEquals(900, cart.getTotalWeightGrams());
        assertTrue(initiallyEmpty.isEmpty());
        assertTrue(other.isEmpty());
        assertTrue(other.getItems().isEmpty());
        assertEquals(0L, other.getSubtotal());
        assertEquals(0, other.getTotalWeightGrams());

        cart.removeItem("A");
        cart.updateQuantity("B", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());
    }
}
