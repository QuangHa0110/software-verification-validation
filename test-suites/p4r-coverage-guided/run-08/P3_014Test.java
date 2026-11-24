package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void updatingKeepsPositionAndRemovingThenReaddingAppendsAtEnd() {
        Product a = new Product("A", "First", 5, 2, 4);
        Product b = new Product("B", "Middle", 7, 3, 50);
        Product c = new Product("C", "Last", 9, 4, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 2);
        cart.addItem(c, 3);
        List<CartItem> snapshot = cart.getItems();

        cart.updateQuantity(new String("B"), 50);
        assertEquals(50, cart.getItems().get(1).getQuantity());
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        assertSame(b, cart.getItems().get(1).getProduct());
        cart.removeItem(new String("B"));
        cart.addItem(b, 4);
        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertSame(a, current.get(0).getProduct());
        assertSame(c, current.get(1).getProduct());
        assertSame(b, current.get(2).getProduct());
        assertEquals(4, current.get(2).getQuantity());

        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(1, new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(c, 1)));
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(3, snapshot.size());
        assertSame(a, snapshot.get(0).getProduct());
        assertEquals(2, snapshot.get(0).getQuantity());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(2, snapshot.get(1).getQuantity());
        assertSame(c, snapshot.get(2).getProduct());
        assertEquals(3, snapshot.get(2).getQuantity());

        b.reduceStock(50);
        cart.updateQuantity("B", 0);
        assertEquals(2, cart.getItems().size());
        assertSame(c, cart.getItems().get(1).getProduct());
        assertEquals(0, b.getStock());
        assertEquals(4, a.getStock());
        assertEquals(3, c.getStock());
    }
}
