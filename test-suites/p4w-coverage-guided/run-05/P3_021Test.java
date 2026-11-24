package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void testCartAddItemValidCumulativeAndOrder() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Item 1", 10000L, 100, 50);
        Product p2 = new Product("P2", "Item 2", 20000L, 200, 50);

        cart.addItem(p1, 2);
        cart.addItem(p2, 3);
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals(80000L, cart.getSubtotal());
        assertEquals(800, cart.getTotalWeightGrams());

        Product p1New = new Product("P1", "Item 1 updated", 15000L, 100, 50);
        cart.addItem(p1New, 3);
        assertEquals(2, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertSame(p1New, cart.getItems().get(0).getProduct());
        assertEquals(5 * 15000L + 3 * 20000L, cart.getSubtotal());
    }
}
