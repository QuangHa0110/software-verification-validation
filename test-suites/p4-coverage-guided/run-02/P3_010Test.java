package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void matchingIdUsesIncomingProductAndSnapshotsKeepPriorLinesInOrder() {
        Cart cart = new Cart();
        Product original = new Product(" A ", "Old", 9, 1, 1);
        Product replacement = new Product(new String(" A "), "New", 12, 5, 50);
        Product b = new Product("B", "B", 2, 3, 2);
        Product c = new Product("C", "C", 3, 4, 1);
        cart.addItem(original, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> before = cart.getItems();

        cart.addItem(replacement, 49);
        List<CartItem> merged = cart.getItems();
        assertEquals(3, merged.size());
        assertSame(replacement, merged.get(0).getProduct());
        assertEquals(50, merged.get(0).getQuantity());
        assertEquals(600L, merged.get(0).getLineTotal());
        assertEquals(607L, cart.getSubtotal());
        assertEquals(260, cart.getTotalWeightGrams());

        cart.updateQuantity(new String(" A "), 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cart.updateQuantity(" A ", 50);
        cart.removeItem("B");
        cart.addItem(b, 1);
        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(50, current.get(0).getQuantity());
        assertSame(c, current.get(1).getProduct());
        assertSame(b, current.get(2).getProduct());
        assertEquals(1, current.get(2).getQuantity());

        assertEquals(3, before.size());
        assertSame(original, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertSame(b, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertSame(c, before.get(2).getProduct());
        assertEquals(1, before.get(2).getQuantity());
        assertSame(b, merged.get(1).getProduct());
        assertEquals(2, merged.get(1).getQuantity());
        assertSame(c, merged.get(2).getProduct());
        assertEquals(50, merged.get(0).getQuantity());

        assertThrows(UnsupportedOperationException.class, () -> current.add(new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> current.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> current.remove(1));
        Iterator<CartItem> iterator = current.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, original.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(2, b.getStock());
        assertEquals(1, c.getStock());
    }
}
