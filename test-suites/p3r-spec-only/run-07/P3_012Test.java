package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void addingEqualIdsReplacesTheProductAtItsOriginalPositionAndAccumulatesQuantity() {
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        Product first = new Product(" A ", "First", 9, 1, 2);
        Product replacement = new Product(new String(" A "), "Replacement", 12, 5, 50);
        Product lower = new Product(" a ", "Lower", 7, 13, 4);
        Cart cart = new Cart();
        cart.addItem(first, 1);
        cart.addItem(lower, 3);
        cart.addItem(first, 1);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 48);

        List<CartItem> after = cart.getItems();
        assertEquals(2, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertEquals(3, after.get(1).getQuantity());
        assertEquals(621L, cart.getSubtotal());
        assertEquals(289, cart.getTotalWeightGrams());
        assertSame(first, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertEquals(2, first.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(4, lower.getStock());
    }
}
