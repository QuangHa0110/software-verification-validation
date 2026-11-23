package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void addMergesEqualIdsUsingIncomingProductAndPreservesEarlierSnapshots() {
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        Product original = new Product(" A ", "Original", 9, 2, 2);
        Product replacement = new Product(new String(" A "), "Replacement", 12, 7, 50);
        Product other = new Product(" a ", "Other", 4, 3, 1);
        Cart cart = new Cart();
        cart.addItem(original, 1);
        cart.addItem(other, 1);
        cart.addItem(original, 1);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 48);

        List<CartItem> after = cart.getItems();
        assertEquals(2, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(other, after.get(1).getProduct());
        assertEquals(1, after.get(1).getQuantity());
        assertEquals(604L, cart.getSubtotal());
        assertEquals(353, cart.getTotalWeightGrams());
        assertEquals(2, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertSame(other, before.get(1).getProduct());
        assertEquals(1, before.get(1).getQuantity());
        assertEquals(2, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(1, other.getStock());
    }
}
