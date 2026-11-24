package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void equalIdAdditionReplacesProductInPlaceAndSnapshotsRetainOriginalLines() {
        Product original = new Product(new String("A"), "Old", 9, 7, 1);
        Product replacement = new Product(new String("A"), "New", 12, 3, 50);
        Product lower = new Product("a", "Lower", 2, 5, 3);
        Product padded = new Product(" A ", "Padded", 4, 10, 3);
        Cart cart = new Cart();
        cart.addItem(original, 1);
        cart.addItem(lower, 1);
        cart.addItem(padded, 1);
        List<CartItem> before = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, before.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> before.add(before.get(0)));
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        cart.addItem(replacement, 49);
        cart.addItem(lower, 1);
        assertEquals(50, cart.getItems().get(0).getQuantity());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(1).getQuantity());
        assertEquals(608L, cart.getSubtotal());
        assertEquals(170, cart.getTotalWeightGrams());
        cart.updateQuantity(new String("A"), 1);
        cart.updateQuantity("A", 50);
        cart.removeItem("a");
        cart.addItem(lower, 3);

        List<CartItem> after = cart.getItems();
        assertEquals(3, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(padded, after.get(1).getProduct());
        assertEquals(1, after.get(1).getQuantity());
        assertSame(lower, after.get(2).getProduct());
        assertEquals(3, after.get(2).getQuantity());
        assertEquals(3, before.size());
        assertSame(original, before.get(0).getProduct());
        assertSame(lower, before.get(1).getProduct());
        assertSame(padded, before.get(2).getProduct());
        for (CartItem item : before) {
            assertEquals(1, item.getQuantity());
        }
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(3, lower.getStock());
        assertEquals(3, padded.getStock());
    }
}
