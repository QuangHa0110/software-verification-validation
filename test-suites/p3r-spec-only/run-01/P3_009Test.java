package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void equalIdsMergeIntoIncomingProductWhileCaseAndSpacesDistinguishOtherLines() {
        Product first = new Product(" A ", "old", 9, 2, 2);
        Product incoming = new Product(new String(" A "), "new", 12, 7, 50);
        Product lower = new Product(" a ", "lower", 5, 11, 3);
        Product plain = new Product("A", "plain", 3, 13, 1);
        Cart cart = new Cart();
        cart.addItem(first, 1);
        cart.addItem(lower, 2);
        cart.addItem(plain, 1);
        cart.addItem(first, 1);
        List<CartItem> before = cart.getItems();
        cart.addItem(incoming, 48);

        List<CartItem> after = cart.getItems();
        assertEquals(3, after.size());
        assertSame(incoming, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertEquals(2, after.get(1).getQuantity());
        assertSame(plain, after.get(2).getProduct());
        assertEquals(1, after.get(2).getQuantity());
        assertEquals(613L, cart.getSubtotal());
        assertEquals(385, cart.getTotalWeightGrams());
        assertSame(first, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertEquals(2, first.getStock());
        assertEquals(50, incoming.getStock());
        assertEquals(3, lower.getStock());
        assertEquals(1, plain.getStock());
    }
}
