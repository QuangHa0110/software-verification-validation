package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void cartCalculationsAndSnapshotImmutability() {
        Cart cart = new Cart();
        Product pA = new Product("A", "Prod A", 12000L, 200, 10);
        Product pB = new Product("B", "Prod B", 7000L, 150, 10);
        cart.addItem(pA, 2);
        cart.addItem(pB, 3);

        assertFalse(cart.isEmpty());
        assertEquals(45000L, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());

        List<CartItem> snapshot = cart.getItems();
        assertEquals(2, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(pA, 1)));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals(2, snapshot.size());
    }
}
