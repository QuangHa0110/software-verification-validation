package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void cartMergesEqualIdsUsingIncomingProductAndFreezesEarlierSnapshots() {
        Cart cart = new Cart();
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        List<CartItem> empty = cart.getItems();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());

        Product first = new Product(" A ", "First", 9, 3, 1);
        Product other = new Product(" a ", "Other", 7, 11, 4);
        Product replacement = new Product(new String(" A "), "Replacement", 1_000_000_000L, 5, 50);
        cart.addItem(first, 1);
        cart.addItem(other, 1);
        cart.addItem(other, 2);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);

        assertTrue(empty.isEmpty());
        assertFalse(cart.isEmpty());
        assertTrue(new Cart().isEmpty());
        assertEquals(2, before.size());
        assertSame(first, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(other, before.get(1).getProduct());
        assertEquals(3, before.get(1).getQuantity());
        List<CartItem> after = cart.getItems();
        assertEquals(2, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(other, after.get(1).getProduct());
        assertEquals(3, after.get(1).getQuantity());
        assertEquals(50_000_000_021L, cart.getSubtotal());
        assertEquals(283, cart.getTotalWeightGrams());
        assertEquals(1, first.getStock());
        assertEquals(4, other.getStock());
        assertEquals(50, replacement.getStock());

        assertThrows(UnsupportedOperationException.class, () -> after.set(0, after.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> after.add(after.get(0)));
        Iterator<CartItem> iterator = after.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
