package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartSnapshotsSurviveUpdateMiddleRemovalAndReinsertionAtEnd() {
        Product a = new Product("A", "A", 2, 3, 4);
        Product b = new Product("B", "B", 5, 7, 50);
        Product c = new Product("C", "C", 11, 13, 3);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 3);
        List<CartItem> snapshot = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(a, 2)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(c, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        cart.updateQuantity("B", 50);
        assertSame(b, cart.getItems().get(1).getProduct());
        assertEquals(50, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        cart.addItem(b, 4);
        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertEquals(List.of(a, c, b), current.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 3, 4), current.stream().map(CartItem::getQuantity).toList());
        assertEquals(List.of(a, b, c), snapshot.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 2, 3), snapshot.stream().map(CartItem::getQuantity).toList());
        assertEquals(4, a.getStock());
        assertEquals(50, b.getStock());
        assertEquals(3, c.getStock());
    }
}
