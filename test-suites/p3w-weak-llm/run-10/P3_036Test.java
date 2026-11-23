package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void testCartGetItemsReturnsImmutableSnapshot() {
        Cart cart = new Cart();
        Product p1 = new Product("A", "A", 10L, 10, 5);
        Product p2 = new Product("B", "B", 20L, 10, 5);
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);

        List<CartItem> snapshot = cart.getItems();
        assertEquals(2, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(p1, 1)));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals(2, snapshot.size());
    }
}
