package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void cartItemOrderingPreservedOnAccumulate() {
        Cart cart = new Cart();
        Product pA = new Product("A", "Món A", 10L, 100, 50);
        Product pB = new Product("B", "Món B", 20L, 200, 50);

        cart.addItem(pA, 2);
        cart.addItem(pB, 3);

        assertEquals(2, cart.getItems().size());
        assertEquals("A", cart.getItems().get(0).getProduct().getId());
        assertEquals("B", cart.getItems().get(1).getProduct().getId());

        cart.addItem(pA, 5);
        assertEquals(2, cart.getItems().size());
        assertEquals("A", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals("B", cart.getItems().get(1).getProduct().getId());
        assertEquals(3, cart.getItems().get(1).getQuantity());
    }
}
