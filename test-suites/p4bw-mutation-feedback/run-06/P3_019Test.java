package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void cartAddItemSuccessAndCalculations() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Pen", 10000L, 100, 20);
        Product p2 = new Product("P2", "Book", 20000L, 200, 10);

        cart.addItem(p1, 2);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(20000L, cart.getSubtotal());
        assertEquals(200, cart.getTotalWeightGrams());

        cart.addItem(p2, 1);
        assertEquals(2, cart.getItems().size());
        assertEquals(40000L, cart.getSubtotal());
        assertEquals(400, cart.getTotalWeightGrams());

        assertEquals("P1", cart.getItems().get(0).getProduct().getId());
        assertEquals("P2", cart.getItems().get(1).getProduct().getId());
    }
}
