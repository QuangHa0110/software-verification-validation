package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void updatingPreservesPositionWhileRemovingAndReaddingAppendsAtEnd() {
        Cart cart = new Cart();
        Product a = new Product("A", "First", 2, 3, 50);
        Product b = new Product("B", "Second", 5, 7, 50);
        Product c = new Product("C", "Third", 11, 13, 1);
        cart.addItem(a, 2);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> snapshot = cart.getItems();

        cart.updateQuantity(new String("B"), 50);
        List<CartItem> updated = cart.getItems();
        assertSame(a, updated.get(0).getProduct());
        assertSame(b, updated.get(1).getProduct());
        assertEquals(50, updated.get(1).getQuantity());
        assertSame(c, updated.get(2).getProduct());
        cart.updateQuantity("B", 1);
        assertEquals(1, cart.getItems().get(1).getQuantity());
        cart.removeItem(new String("B"));
        cart.addItem(b, 3);
        assertEquals(List.of(a, c, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(3, cart.getItems().get(2).getQuantity());
        assertEquals(3, snapshot.size());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(2, snapshot.get(1).getQuantity());

        b.reduceStock(50);
        cart.updateQuantity("B", 0);
        assertEquals(List.of(a, c), cart.getItems().stream().map(CartItem::getProduct).toList());
        cart.removeItem("A");
        cart.updateQuantity("C", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(50, a.getStock());
        assertEquals(0, b.getStock());
        assertEquals(1, c.getStock());
    }
}
