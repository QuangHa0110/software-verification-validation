package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void equalIdsReplaceProductInPlaceAndCartSnapshotsKeepOriginalLines() {
        Product first = new Product(" A ", "Old", 9, 2, 1);
        Product lower = new Product(" a ", "Lower", 7, 3, 4);
        Product plain = new Product("A", "Plain", 19, 5, 3);
        Product replacement = new Product(new String(" A "), "New", 12, 11, 50);
        Cart cart = new Cart();
        cart.addItem(first, 1);
        cart.addItem(lower, 2);
        cart.addItem(plain, 3);
        List<CartItem> before = cart.getItems();

        cart.addItem(replacement, 49);
        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(lower, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertSame(plain, merged.get(2).getProduct());
        assertEquals(3, merged.get(2).getQuantity());
        assertEquals(671L, cart.getSubtotal());
        assertEquals(571, cart.getTotalWeightGrams());

        cart.updateQuantity(" A ", 1);
        cart.updateQuantity(" A ", 50);
        cart.removeItem(" a ");
        cart.addItem(lower, 4);
        List<CartItem> reordered = cart.getItems();
        assertEquals(3, reordered.size());
        assertSame(replacement, reordered.get(0).getProduct());
        assertEquals(50, reordered.get(0).getQuantity());
        assertSame(plain, reordered.get(1).getProduct());
        assertEquals(3, reordered.get(1).getQuantity());
        assertSame(lower, reordered.get(2).getProduct());
        assertEquals(4, reordered.get(2).getQuantity());

        assertEquals(3, before.size());
        assertSame(first, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(lower, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertSame(plain, before.get(2).getProduct());
        assertEquals(3, before.get(2).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> before.add(new CartItem(first, 1)));
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, new CartItem(lower, 1)));
        assertThrows(UnsupportedOperationException.class, () -> before.remove(0));
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, first.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(4, lower.getStock());
        assertEquals(3, plain.getStock());
    }
}
