package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void cartRemoveItemAndSnapshotImmutability() {
        Cart cart = new Cart();
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("P1"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(""));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));

        Product a = new Product("A", "A", 12000L, 200, 10);
        Product b = new Product("B", "B", 7000L, 150, 10);
        cart.addItem(a, 2);
        cart.addItem(b, 3);

        List<CartItem> snapshot = cart.getItems();
        assertEquals(2, snapshot.size());
        assertEquals(45000L, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("NON_EXISTING"));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, snapshot.size());

        cart.addItem(a, 1);
        assertEquals(2, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());
        assertEquals("A", cart.getItems().get(1).getProduct().getId());
    }
}
