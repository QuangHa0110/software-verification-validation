package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void cartAddItemNewProductCalculatesTotals() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 10);
        cart.addItem(p1, 2);
        assertFalse(cart.isEmpty());
        assertEquals(200000L, cart.getSubtotal());
        assertEquals(400, cart.getTotalWeightGrams());
        assertEquals(1, cart.getItems().size());
        assertEquals("P01", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
