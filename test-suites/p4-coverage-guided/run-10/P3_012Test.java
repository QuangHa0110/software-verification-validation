package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartSnapshotSurvivesUpdatesMiddleRemovalAndReinsertion() {
        Cart cart = new Cart();
        Product a = new Product("A", "First", 12, 1, 2);
        Product b = new Product("a", "Middle", 15, 3, 50);
        Product c = new Product("C", "Last", 7, 4, 4);
        cart.addItem(a, 2);
        cart.addItem(b, 2);
        cart.addItem(c, 3);
        List<CartItem> snapshot = cart.getItems();
        CartItem extra = new CartItem(c, 1);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(extra));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, extra));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        cart.updateQuantity("a", 50);
        List<CartItem> updated = cart.getItems();
        assertSame(b, updated.get(1).getProduct());
        assertEquals(50, updated.get(1).getQuantity());
        cart.updateQuantity("a", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        cart.removeItem("a");
        assertEquals(List.of(a, c), cart.getItems().stream().map(CartItem::getProduct).toList());
        cart.addItem(b, 4);
        assertEquals(List.of(a, c, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 3, 4), cart.getItems().stream().map(CartItem::getQuantity).toList());
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 2, 3), snapshot.stream().map(CartItem::getQuantity).toList());
        assertEquals(50, updated.get(1).getQuantity());
        assertEquals(2, a.getStock());
        assertEquals(50, b.getStock());
        assertEquals(4, c.getStock());
    }
}
