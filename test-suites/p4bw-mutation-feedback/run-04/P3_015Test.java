package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void cartAddItemValidMultipleAndTotals() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Item 1", 12000L, 200, 10);
        Product p2 = new Product("P2", "Item 2", 7000L, 150, 10);

        cart.addItem(p1, 2);
        cart.addItem(p2, 3);

        assertFalse(cart.isEmpty());
        assertEquals(45000L, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertEquals("P1", items.get(0).getProduct().getId());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals("P2", items.get(1).getProduct().getId());
        assertEquals(3, items.get(1).getQuantity());
    }
}
