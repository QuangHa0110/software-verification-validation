package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void cartUpdateQuantitySuccessAndRemovalOnZero() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 100, 20);
        cart.addItem(p, 5);
        cart.updateQuantity("P1", 10);
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertEquals(100000L, cart.getSubtotal());

        cart.updateQuantity("P1", 0);
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
    }
}
