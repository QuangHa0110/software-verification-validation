package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void cartReplacementUsesIncomingProductAndSnapshotsKeepEarlierLines() {
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        Product original = new Product(" A ", "Original", 9, 3, 1);
        Product replacement = new Product(new String(" A "), "Replacement", 1_000_000_000L, 20, 50);
        Product lower = new Product(" a ", "Lowercase", 7, 5, 3);
        Product plain = new Product("A", "Unpadded", 11, 2, 1);
        Cart cart = new Cart();
        List<CartItem> empty = cart.getItems();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());

        cart.addItem(original, 1);
        cart.addItem(lower, 1);
        cart.addItem(plain, 1);
        cart.addItem(lower, 2);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);
        assertFalse(cart.isEmpty());
        assertTrue(new Cart().isEmpty());
        assertTrue(empty.isEmpty());
        List<CartItem> after = cart.getItems();
        assertEquals(3, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertEquals(3, after.get(1).getQuantity());
        assertSame(plain, after.get(2).getProduct());
        assertEquals(1, after.get(2).getQuantity());
        assertEquals(50_000_000_032L, cart.getSubtotal());
        assertEquals(1_017, cart.getTotalWeightGrams());

        cart.removeItem(" a ");
        cart.addItem(lower, 1);
        cart.updateQuantity(" A ", 2);
        List<CartItem> reordered = cart.getItems();
        assertEquals(3, reordered.size());
        assertSame(replacement, reordered.get(0).getProduct());
        assertEquals(2, reordered.get(0).getQuantity());
        assertSame(plain, reordered.get(1).getProduct());
        assertSame(lower, reordered.get(2).getProduct());
        assertEquals(1, reordered.get(2).getQuantity());

        assertEquals(3, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(lower, before.get(1).getProduct());
        assertEquals(3, before.get(1).getQuantity());
        assertSame(plain, before.get(2).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertThrows(UnsupportedOperationException.class, () -> before.remove(1));
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, before.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> before.add(before.get(0)));
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(3, lower.getStock());
        assertEquals(1, plain.getStock());
    }
}
