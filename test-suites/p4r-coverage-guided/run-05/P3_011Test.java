package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartSnapshotsRejectDirectAndIteratorMutations() {
        Cart cart = new Cart();
        Product p = new Product("P", "Pen", 9, 2, 1);
        CartItem extra = new CartItem(p, 2);
        List<CartItem> empty = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> empty.add(extra));
        cart.addItem(p, 1);
        assertTrue(empty.isEmpty());
        List<CartItem> items = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(extra));
        assertThrows(UnsupportedOperationException.class, () -> items.set(0, extra));
        assertThrows(UnsupportedOperationException.class, () -> items.remove(0));
        assertThrows(UnsupportedOperationException.class, items::clear);
        ListIterator<CartItem> iterator = items.listIterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> iterator.set(extra));
        assertThrows(UnsupportedOperationException.class, () -> iterator.add(extra));
        assertEquals(1, items.size());
        assertSame(p, items.get(0).getProduct());
        assertEquals(1, items.get(0).getQuantity());
    }
}
