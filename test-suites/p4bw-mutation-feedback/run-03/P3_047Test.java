package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_047Test {

    @Test
    void cartUpdateQuantitySuccess() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 50);
        cart.addItem(p, 2);

        cart.updateQuantity("P1", 50);
        assertEquals(50, cart.getItems().get(0).getQuantity());

        cart.updateQuantity("P1", 0);
        assertTrue(cart.isEmpty());
    }
}
