package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartSnapshotRejectsBothDirectAndIteratorMutations() {
        Product product = new Product("P", "Pen", 3, 1, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        List<CartItem> snapshot = cart.getItems();
        CartItem other = new CartItem(product, 1);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(other));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, other));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertEquals(1, snapshot.size());
        assertSame(product, snapshot.get(0).getProduct());
        assertEquals(2, snapshot.get(0).getQuantity());
        assertEquals(2, product.getStock());
    }
}
