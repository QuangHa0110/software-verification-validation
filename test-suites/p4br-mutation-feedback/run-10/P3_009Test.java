package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartMergesEqualIdsWithIncomingProductAndPreservesEarlierSnapshots() {
        Cart cart = new Cart();
        Product original = new Product(" A ", "original", 9, 4, 1);
        Product lower = new Product(" a ", "lower", 7, 2, 2);
        Product unpadded = new Product("A", "unpadded", 6, 1, 3);
        Product replacement = new Product(new String(" A "), "replacement", 12, 3, 50);
        cart.addItem(original, 1);
        cart.addItem(lower, 2);
        cart.addItem(unpadded, 3);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);
        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertEquals(632L, cart.getSubtotal());
        assertEquals(157, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cart.updateQuantity(" A ", 50);
        cart.removeItem(new String(" a "));
        cart.addItem(lower, 1);
        List<CartItem> reordered = cart.getItems();
        assertEquals(3, reordered.size());
        assertSame(replacement, reordered.get(0).getProduct());
        assertSame(unpadded, reordered.get(1).getProduct());
        assertSame(lower, reordered.get(2).getProduct());
        assertEquals(50, reordered.get(0).getQuantity());
        assertEquals(3, reordered.get(1).getQuantity());
        assertEquals(1, reordered.get(2).getQuantity());

        assertEquals(3, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(lower, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertSame(unpadded, before.get(2).getProduct());
        assertEquals(3, before.get(2).getQuantity());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(lower, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(2, lower.getStock());
        assertEquals(3, unpadded.getStock());
    }
}
