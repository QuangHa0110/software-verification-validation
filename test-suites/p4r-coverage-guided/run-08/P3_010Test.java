package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void mergingEqualIdsUsesIncomingProductAndPreservesPositionAndOldSnapshot() {
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        Cart cart = new Cart();
        Product original = new Product(new String(" A "), "Old", 9, 3, 2);
        Product replacement = new Product(new String(" A "), "New", 12, 5, 50);
        Product lower = new Product(" a ", "Lower", 7, 2, 1);
        Product unpadded = new Product("A", "Unpadded", 4, 1, 1);
        cart.addItem(original, 1);
        cart.addItem(lower, 1);
        cart.addItem(unpadded, 1);
        cart.addItem(original, 1);
        List<CartItem> before = cart.getItems();

        cart.addItem(replacement, 48);
        List<CartItem> after = cart.getItems();
        assertEquals(3, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertSame(unpadded, after.get(2).getProduct());
        assertEquals(611L, cart.getSubtotal());
        assertEquals(253, cart.getTotalWeightGrams());
        assertSame(original, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertEquals(2, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(1, lower.getStock());
        assertEquals(1, unpadded.getStock());
    }
}
