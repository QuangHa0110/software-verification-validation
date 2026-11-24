package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartMatchesExactIdsReplacesProductsInPlaceAndFreezesEarlierSnapshots() {
        Product old = new Product(" A ", "old", 9, 2, 1);
        Product replacement = new Product(new String(" A "), "new", 12, 5, 50);
        Product plain = new Product("A", "plain", 3, 7, 2);
        Product lower = new Product("a", "lower", 4, 11, 1);
        Cart cart = new Cart();
        cart.addItem(old, 1);
        cart.addItem(plain, 2);
        cart.addItem(lower, 1);
        List<CartItem> before = cart.getItems();

        cart.addItem(replacement, 49);
        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(plain, merged.get(1).getProduct());
        assertSame(lower, merged.get(2).getProduct());
        assertEquals(610L, cart.getSubtotal());
        assertEquals(275, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cart.updateQuantity(" A ", 50);
        cart.removeItem(new String("A"));
        assertEquals(2, cart.getItems().size());
        assertSame(lower, cart.getItems().get(1).getProduct());
        cart.addItem(plain, 1);
        List<CartItem> current = cart.getItems();
        assertSame(replacement, current.get(0).getProduct());
        assertSame(lower, current.get(1).getProduct());
        assertSame(plain, current.get(2).getProduct());
        assertEquals(1, current.get(2).getQuantity());

        assertEquals(3, before.size());
        assertSame(old, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(plain, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertSame(lower, before.get(2).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> current.add(before.get(0)));
        assertThrows(UnsupportedOperationException.class, () -> current.set(0, before.get(0)));
        assertThrows(UnsupportedOperationException.class, () -> current.remove(0));
        Iterator<CartItem> iterator = current.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, old.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(2, plain.getStock());
        assertEquals(1, lower.getStock());
    }
}
