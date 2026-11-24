package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void removingMiddleLineAndReaddingItAppendsWithoutChangingReadOnlySnapshot() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 1, 1, 3);
        Product b = new Product("a", "B", 2, 2, 3);
        Product c = new Product(" A ", "C", 3, 3, 3);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 3);
        List<CartItem> before = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> before.add(new CartItem(a, 1)));
        assertThrows(UnsupportedOperationException.class, () -> before.set(0, new CartItem(b, 1)));
        assertThrows(UnsupportedOperationException.class, () -> before.remove(1));
        Iterator<CartItem> iterator = before.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);

        cart.removeItem(new String("a"));
        cart.addItem(b, 1);
        List<CartItem> after = cart.getItems();
        assertEquals(3, after.size());
        assertSame(a, after.get(0).getProduct());
        assertSame(c, after.get(1).getProduct());
        assertSame(b, after.get(2).getProduct());
        assertEquals(1, after.get(0).getQuantity());
        assertEquals(3, after.get(1).getQuantity());
        assertEquals(1, after.get(2).getQuantity());
        assertEquals(3, before.size());
        assertSame(b, before.get(1).getProduct());
        assertEquals(2, before.get(1).getQuantity());
        assertSame(c, before.get(2).getProduct());
    }
}
