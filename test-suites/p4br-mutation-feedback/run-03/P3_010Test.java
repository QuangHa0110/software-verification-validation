package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void duplicateIdsAccumulateUsingIncomingProductAndPreserveInsertionPosition() {
        Product original = new Product(" A ", "Old", 9, 2, 2);
        Product lower = new Product(" a ", "Lower", 7, 3, 4);
        Product replacement = new Product(new String(" A "), "New", 12, 5, 50);
        Cart cart = new Cart();
        cart.addItem(original, 1);
        cart.addItem(lower, 2);
        cart.addItem(original, 1);
        List<CartItem> before = cart.getItems();

        cart.addItem(replacement, 48);
        List<CartItem> after = cart.getItems();
        assertEquals(2, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(lower, after.get(1).getProduct());
        assertEquals(2, after.get(1).getQuantity());
        assertEquals(614L, cart.getSubtotal());
        assertEquals(256, cart.getTotalWeightGrams());
        assertEquals(2, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertSame(lower, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertEquals(2, original.getStock());
        assertEquals(4, lower.getStock());
        assertEquals(50, replacement.getStock());
    }
}
