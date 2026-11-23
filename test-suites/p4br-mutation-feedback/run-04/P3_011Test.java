package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void updatesKeepPositionAndRemovingThenReaddingMovesOnlyThatLineToTheEnd() {
        Product a = new Product("A", "A", 1, 1, 1);
        Product b = new Product("B", "B", 2, 2, 50);
        Product c = new Product("C", "C", 3, 3, 1);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> snapshot = cart.getItems();
        Iterator<CartItem> iterator = snapshot.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(1));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new CartItem(c, 1)));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, new CartItem(b, 1)));

        cart.updateQuantity(new String("B"), 50);
        assertSame(b, cart.getItems().get(1).getProduct());
        assertEquals(50, cart.getItems().get(1).getQuantity());
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        cart.addItem(b, 3);
        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertSame(a, current.get(0).getProduct());
        assertSame(c, current.get(1).getProduct());
        assertSame(b, current.get(2).getProduct());
        assertEquals(3, current.get(2).getQuantity());
        assertEquals(3, snapshot.size());
        assertSame(a, snapshot.get(0).getProduct());
        assertSame(b, snapshot.get(1).getProduct());
        assertSame(c, snapshot.get(2).getProduct());
        assertEquals(2, snapshot.get(1).getQuantity());

        b.reduceStock(50);
        cart.updateQuantity("B", 0);
        assertEquals(2, cart.getItems().size());
        assertEquals(4L, cart.getSubtotal());
        assertEquals(4, cart.getTotalWeightGrams());
        assertEquals(0, b.getStock());
    }
}
