package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void cartAddItemSuccessAndCumulativeUpdates() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "A", 9L, 10, 1);
        cart.addItem(p1, 1);

        Product p2 = new Product("P2", "B", 20L, 10, 5);
        cart.addItem(p2, 1);

        Product p1New = new Product("P1", "A-new", 12L, 15, 50);
        cart.addItem(p1New, 49);

        assertEquals(2, cart.getItems().size());
        assertEquals("P1", cart.getItems().get(0).getProduct().getId());
        assertEquals(12L, cart.getItems().get(0).getProduct().getPrice());
        assertEquals(50, cart.getItems().get(0).getQuantity());
        assertEquals(600L, cart.getItems().get(0).getLineTotal());

        assertEquals("P2", cart.getItems().get(1).getProduct().getId());
        assertEquals(1, cart.getItems().get(1).getQuantity());

        assertEquals(620L, cart.getSubtotal());
        assertEquals(15 * 50 + 10 * 1, cart.getTotalWeightGrams());
    }
}
