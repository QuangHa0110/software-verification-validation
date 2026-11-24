package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void cartGetItemsReturnsUnmodifiableSnapshot() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 20);
        cart.addItem(p, 2);

        List<CartItem> snapshot = cart.getItems();
        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(p, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());

        cart.addItem(p, 1);
        assertEquals(1, snapshot.size());
        assertEquals(2, snapshot.get(0).getQuantity());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }
}
