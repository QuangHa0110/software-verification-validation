package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartUsesExactIdsAndReplacesAnEqualIdInPlaceUsingIncomingStock() {
        Product original = new Product(" A ", "Old", 9, 3, 1);
        Product replacement = new Product(new String(" A "), "New", 12, 7, 50);
        Product lower = new Product(" a ", "Lower", 5, 2, 3);
        Product plain = new Product("A", "Plain", 11, 4, 1);
        Cart cart = new Cart();
        cart.addItem(original, 1);
        cart.addItem(lower, 2);
        cart.addItem(plain, 1);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);

        List<CartItem> items = cart.getItems();
        assertEquals(3, items.size());
        assertSame(replacement, items.get(0).getProduct());
        assertEquals(50, items.get(0).getQuantity());
        assertSame(lower, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
        assertSame(plain, items.get(2).getProduct());
        assertEquals(1, items.get(2).getQuantity());
        assertEquals(621L, cart.getSubtotal());
        assertEquals(358, cart.getTotalWeightGrams());
        assertSame(original, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(3, lower.getStock());
        assertEquals(1, plain.getStock());
    }
}
