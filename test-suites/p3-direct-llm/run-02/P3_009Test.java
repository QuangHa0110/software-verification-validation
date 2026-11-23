package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartsAreIndependentAndAccumulateLineTotalsUsingLongs() {
        Cart cart = new Cart();
        List<CartItem> emptySnapshot = cart.getItems();
        assertTrue(cart.isEmpty());
        assertTrue(emptySnapshot.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);

        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("a", "B", 7_000, 150, 2);
        cart.addItem(a, 1);
        cart.addItem(a, 2);
        assertEquals(3_000_000_000L, cart.getSubtotal());
        cart.addItem(b, 2);
        assertFalse(cart.isEmpty());
        assertTrue(new Cart().isEmpty());
        assertTrue(emptySnapshot.isEmpty());
        assertEquals(3_000_014_000L, cart.getSubtotal());
        assertEquals(900, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());

        cart.removeItem(new String("A"));
        assertSame(b, cart.getItems().get(0).getProduct());
        cart.updateQuantity("a", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(2, b.getStock());
    }
}
