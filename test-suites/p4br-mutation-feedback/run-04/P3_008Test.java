package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void independentCartsHaveZeroTotalsWhenEmptyAndAccumulateMoneyAsLong() {
        Cart cart = new Cart();
        List<CartItem> emptySnapshot = cart.getItems();
        Product p = new Product("A", "A", 1_000_000_000L, 200, 3);
        cart.addItem(p, 3);
        Cart other = new Cart();
        assertTrue(other.isEmpty());
        assertTrue(other.getItems().isEmpty());
        assertEquals(0L, other.getSubtotal());
        assertEquals(0, other.getTotalWeightGrams());
        assertTrue(emptySnapshot.isEmpty());
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(600, cart.getTotalWeightGrams());
        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, p.getStock());
    }
}
