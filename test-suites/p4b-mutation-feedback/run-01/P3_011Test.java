package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartSnapshotSurvivesUpdateMiddleRemovalAndReinsertion() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 1, 1, 50);
        Product b = new Product("B", "B", 2, 2, 2);
        Product c = new Product("C", "C", 3, 3, 1);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> snapshot = cart.getItems();
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, snapshot.get(1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(snapshot.get(0)));

        cart.updateQuantity(new String("A"), 50);
        cart.removeItem(new String("B"));
        cart.addItem(b, 1);
        List<CartItem> current = cart.getItems();
        assertEquals(List.of(a, c, b), current.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(50, 1, 1), current.stream().map(CartItem::getQuantity).toList());
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 2, 1), snapshot.stream().map(CartItem::getQuantity).toList());
        cart.updateQuantity("A", 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(50, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(1, c.getStock());
    }
}
