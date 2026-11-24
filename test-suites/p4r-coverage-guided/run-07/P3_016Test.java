package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void cartSnapshotsSurviveUpdatesMiddleRemovalAndReinsertionAtTheEnd() {
        Product a = new Product("A", "First", 2, 3, 50);
        Product b = new Product("B", "Middle", 5, 7, 50);
        Product c = new Product("C", "Last", 11, 13, 50);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        cart.addItem(c, 4);
        List<CartItem> snapshot = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(1, new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        Iterator<CartItem> it = snapshot.iterator();
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);

        cart.updateQuantity(new String("B"), 50);
        assertSame(b, cart.getItems().get(1).getProduct());
        assertEquals(50, cart.getItems().get(1).getQuantity());
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        assertEquals(List.of(a, c), cart.getItems().stream().map(CartItem::getProduct).toList());
        cart.addItem(b, 5);
        assertEquals(List.of(a, c, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 4, 5), cart.getItems().stream().map(CartItem::getQuantity).toList());
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 3, 4), snapshot.stream().map(CartItem::getQuantity).toList());
        assertEquals(50, a.getStock());
        assertEquals(50, b.getStock());
        assertEquals(50, c.getStock());
    }
}
