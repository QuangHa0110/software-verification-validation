package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @Test
    void cartMergesByExactIdUsingIncomingProductAndPreservesPreviousSnapshots() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        List<CartItem> empty = cart.getItems();
        Product original = new Product(" A ", "First", 9, 7, 1);
        Product differentCase = new Product(" a ", "Second", 3, 5, 4);
        Product unpadded = new Product("A", "Third", 2, 3, 2);
        Product replacement = new Product(new String(" A "), "Replacement", 1_000_000_000L, 11, 50);
        cart.addItem(original, 1);
        cart.addItem(differentCase, 1);
        cart.addItem(differentCase, 2);
        cart.addItem(unpadded, 2);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);

        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        assertFalse(cart.isEmpty());
        assertTrue(new Cart().isEmpty());
        assertTrue(empty.isEmpty());
        assertEquals(3, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(differentCase, merged.get(1).getProduct());
        assertEquals(3, merged.get(1).getQuantity());
        assertSame(unpadded, merged.get(2).getProduct());
        assertEquals(2, merged.get(2).getQuantity());
        assertEquals(50_000_000_013L, cart.getSubtotal());
        assertEquals(571, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cart.updateQuantity(" A ", 50);
        assertEquals(50, cart.getItems().get(0).getQuantity());
        cart.removeItem(new String(" a "));
        cart.addItem(differentCase, 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertSame(unpadded, cart.getItems().get(1).getProduct());
        assertSame(differentCase, cart.getItems().get(2).getProduct());
        cart.updateQuantity(" A ", 0);
        cart.removeItem("A");
        cart.updateQuantity(" a ", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(50, merged.get(0).getQuantity());
        assertEquals(3, merged.get(1).getQuantity());
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(4, differentCase.getStock());
        assertEquals(2, unpadded.getStock());

        assertThrows(UnsupportedOperationException.class, () -> merged.add(new CartItem(original, 1)));
        assertThrows(UnsupportedOperationException.class, () -> merged.set(0, new CartItem(original, 1)));
        assertThrows(UnsupportedOperationException.class, () -> merged.remove(0));
        Iterator<CartItem> iterator = merged.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
