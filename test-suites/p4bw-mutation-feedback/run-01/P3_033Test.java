package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @Test
    void cartAddItemNewItemSuccess() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 150, 10);
        cart.addItem(p, 2);
        assertFalse(cart.isEmpty());
        assertEquals(20000L, cart.getSubtotal());
        assertEquals(300, cart.getTotalWeightGrams());
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals("P1", cart.getItems().get(0).getProduct().getId());
    }
}
