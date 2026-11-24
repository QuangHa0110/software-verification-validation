package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void cartUpdateQuantityZeroRemovesItem() {
        Cart cart = new Cart();
        Product p = new Product("A", "Product A", 100L, 10, 10);
        cart.addItem(p, 5);
        assertEquals(1, cart.getItems().size());

        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }
}
