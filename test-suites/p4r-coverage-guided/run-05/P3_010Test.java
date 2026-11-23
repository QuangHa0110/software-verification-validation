package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartMergesEqualIdsUsingIncomingProductAndPreservesPreviousSnapshot() {
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        Product old = new Product(" A ", "Old", 9, 4, 1);
        Product replacement = new Product(new String(" A "), "New", 12, 5, 50);
        Product lower = new Product(" a ", "Lower", 7, 3, 2);
        Product plain = new Product("A", "Plain", 2, 1, 1);
        Cart cart = new Cart();
        cart.addItem(old, 1);
        cart.addItem(lower, 2);
        cart.addItem(plain, 1);
        List<CartItem> initial = cart.getItems();
        cart.addItem(replacement, 49);

        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(lower, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertSame(plain, merged.get(2).getProduct());
        assertEquals(1, merged.get(2).getQuantity());
        assertEquals(616L, cart.getSubtotal());
        assertEquals(257, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        cart.removeItem(new String(" a "));
        cart.addItem(lower, 1);
        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(1, current.get(0).getQuantity());
        assertSame(plain, current.get(1).getProduct());
        assertSame(lower, current.get(2).getProduct());
        assertEquals(1, current.get(2).getQuantity());
        assertSame(old, initial.get(0).getProduct());
        assertEquals(1, initial.get(0).getQuantity());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(lower, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertEquals(1, old.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(2, lower.getStock());
        assertEquals(1, plain.getStock());
    }
}
