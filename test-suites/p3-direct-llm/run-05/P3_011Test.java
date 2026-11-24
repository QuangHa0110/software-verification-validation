package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartSnapshotsKeepOldQuantitiesAndOrderAfterMiddleRemovalAndReinsertion() {
        Product a = new Product("A", "First", 1, 1, 2);
        Product b = new Product("B", "Middle", 2, 2, 50);
        Product c = new Product("C", "Last", 3, 3, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        cart.addItem(c, 1);
        List<CartItem> snapshot = cart.getItems();

        cart.updateQuantity(new String("B"), 50);
        assertSame(b, cart.getItems().get(1).getProduct());
        assertEquals(50, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        cart.addItem(b, 1);
        cart.updateQuantity("A", 1);
        List<CartItem> current = cart.getItems();
        assertEquals(List.of(a, c, b), current.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 1, 1), current.stream().map(CartItem::getQuantity).toList());
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 3, 1), snapshot.stream().map(CartItem::getQuantity).toList());

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(2, a.getStock());
        assertEquals(50, b.getStock());
        assertEquals(3, c.getStock());
    }
}
