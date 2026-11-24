package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void mergingAnEqualIdUsesIncomingProductAndKeepsOldSnapshotAndPosition() {
        Cart cart = new Cart();
        Product old = new Product(" A ", "old", 9, 2, 2);
        Product other = new Product("a", "other", 7, 3, 1);
        Product replacement = new Product(new String(" A "), "new", 12, 5, 50);
        cart.addItem(old, 1);
        cart.addItem(other, 1);
        cart.addItem(old, 1);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 48);

        List<CartItem> merged = cart.getItems();
        assertEquals(2, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(other, merged.get(1).getProduct());
        assertEquals(1, merged.get(1).getQuantity());
        assertEquals(607L, cart.getSubtotal());
        assertEquals(253, cart.getTotalWeightGrams());
        cart.updateQuantity(new String(" A "), 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(50, merged.get(0).getQuantity());
        assertSame(old, before.get(0).getProduct());
        assertEquals(2, before.get(0).getQuantity());
        assertEquals(18L, before.get(0).getLineTotal());
        assertEquals(2, old.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(1, other.getStock());
    }
}
