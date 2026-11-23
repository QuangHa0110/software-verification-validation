package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartMergesEqualIdsUsingIncomingProductAndPreservesEarlierSnapshot() {
        Product first = new Product(" A ", "old", 9, 7, 1);
        Product second = new Product(" a ", "case variant", 3, 2, 2);
        Product replacement = new Product(new String(" A "), "new", 12, 11, 50);
        Cart cart = new Cart();
        cart.addItem(first, 1);
        cart.addItem(second, 2);
        List<CartItem> before = cart.getItems();
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> before.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, new CartItem(second, 1)));
        assertThrows(UnsupportedOperationException.class, () -> before.add(new CartItem(second, 1)));

        cart.addItem(replacement, 49);
        List<CartItem> merged = cart.getItems();
        assertEquals(2, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(second, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertEquals(606L, cart.getSubtotal());
        assertEquals(554, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(50, merged.get(0).getQuantity());
        assertEquals(2, before.size());
        assertSame(first, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(second, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertEquals(1, first.getStock());
        assertEquals(2, second.getStock());
        assertEquals(50, replacement.getStock());
    }
}
