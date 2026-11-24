package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartsAreIndependentAndReturnZeroTotalsAfterLastLineIsRemoved() {
        Cart cart = new Cart();
        Product p = new Product("P", "Product", 1_000_000_000L, 17, 3);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        cart.addItem(p, 2);
        cart.addItem(p, 1);
        Cart other = new Cart();
        assertTrue(other.isEmpty());
        assertTrue(other.getItems().isEmpty());
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(51, cart.getTotalWeightGrams());
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, p.getStock());
    }
}
