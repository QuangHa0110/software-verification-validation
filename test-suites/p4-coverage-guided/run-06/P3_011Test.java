package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void removingMiddleLineAndAddingItBackMovesItToEnd() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 2, 3, 50);
        Product b = new Product("B", "B", 5, 7, 50);
        Product c = new Product("C", "C", 11, 13, 50);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 3);
        cart.updateQuantity("B", 50);
        List<CartItem> snapshot = cart.getItems();
        cart.removeItem(new String("B"));
        cart.addItem(b, 1);

        List<CartItem> current = cart.getItems();
        assertEquals(3, current.size());
        assertSame(a, current.get(0).getProduct());
        assertEquals(1, current.get(0).getQuantity());
        assertSame(c, current.get(1).getProduct());
        assertEquals(3, current.get(1).getQuantity());
        assertSame(b, current.get(2).getProduct());
        assertEquals(1, current.get(2).getQuantity());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(50, snapshot.get(1).getQuantity());
        assertEquals(50, b.getStock());
    }
}
