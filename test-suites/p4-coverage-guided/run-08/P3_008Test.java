package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void cartMergesEqualIdsUsingIncomingProductAndLeavesOldSnapshotIntact() {
        Cart cart = new Cart();
        Product old = new Product(" A ", "Old", 9, 2, 1);
        Product other = new Product(" a ", "Other", 1_000_000_000L, 3, 3);
        Product replacement = new Product(new String(" A "), "New", 12, 5, 50);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());

        cart.addItem(old, 1);
        cart.addItem(other, 1);
        cart.addItem(other, 2);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);
        assertFalse(cart.isEmpty());
        assertTrue(new Cart().isEmpty());
        List<CartItem> current = cart.getItems();
        assertEquals(2, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(50, current.get(0).getQuantity());
        assertSame(other, current.get(1).getProduct());
        assertEquals(3, current.get(1).getQuantity());
        assertEquals(3_000_000_600L, cart.getSubtotal());
        assertEquals(259, cart.getTotalWeightGrams());
        assertEquals(1, old.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(3, other.getStock());

        assertEquals(2, before.size());
        assertSame(old, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertEquals(9L, before.get(0).getLineTotal());
        assertSame(other, before.get(1).getProduct());
        assertEquals(3, before.get(1).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, before.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> before.add(before.get(0)));
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
