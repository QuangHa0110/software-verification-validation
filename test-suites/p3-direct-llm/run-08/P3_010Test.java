package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void quantityUpdatesKeepPositionAndReinsertionAppendsAfterSurvivingLines() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 2, 3, 1);
        Product b = new Product("B", "B", 3, 5, 50);
        Product c = new Product("C", "C", 5, 7, 1);
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        cart.addItem(c, 1);
        List<CartItem> original = cart.getItems();
        cart.updateQuantity(new String("B"), 50);
        List<CartItem> updated = cart.getItems();
        assertEquals(List.of(a, b, c), updated.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 50, 1), updated.stream().map(CartItem::getQuantity).toList());
        cart.removeItem(new String("B"));
        assertEquals(List.of(a, c), cart.getItems().stream().map(CartItem::getProduct).toList());
        cart.addItem(b, 4);
        cart.updateQuantity("B", 1);
        assertEquals(List.of(a, c, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 1, 1), cart.getItems().stream().map(CartItem::getQuantity).toList());
        b.reduceStock(50);
        cart.updateQuantity("B", 0);
        cart.removeItem("A");
        cart.updateQuantity("C", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(List.of(a, b, c), original.stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 2, 1), original.stream().map(CartItem::getQuantity).toList());
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());
        assertEquals(1, c.getStock());
    }
}
