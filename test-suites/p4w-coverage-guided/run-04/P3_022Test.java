package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void cartGetItemsSnapshotImmutability() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Item", 100L, 100, 10);
        cart.addItem(p, 1);

        List<CartItem> snapshot = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(p, 1)));

        cart.addItem(p, 1);
        assertEquals(1, snapshot.get(0).getQuantity());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
