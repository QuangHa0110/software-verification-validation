package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void updatingPreservesPositionAndReinsertingAMiddleLineAppendsIt() {
        Cart cart = new Cart();
        Product a = new Product("A", "First", 1, 1, 1);
        Product b = new Product("B", "Middle", 2, 2, 50);
        Product c = new Product("C", "Last", 3, 3, 1);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> snapshot = cart.getItems();

        cart.updateQuantity(new String("B"), 50);
        List<CartItem> updated = cart.getItems();
        assertEquals(List.of(a, b, c), updated.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 50, 1), updated.stream().map(CartItem::getQuantity).toList());
        assertSame(b, updated.get(1).getProduct());
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        assertEquals(List.of(a, c), cart.getItems().stream().map(CartItem::getProduct).toList());
        cart.addItem(b, 3);
        List<CartItem> current = cart.getItems();
        assertEquals(List.of(a, c, b), current.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 1, 3), current.stream().map(CartItem::getQuantity).toList());

        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, snapshot.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(snapshot.get(0)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 2, 1), snapshot.stream().map(CartItem::getQuantity).toList());
        assertEquals(50, b.getStock());
    }
}
