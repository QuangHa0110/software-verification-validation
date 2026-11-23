package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_045Test {

    @Test
    void cartUpdateQuantityExceedsMaxQtyThrows() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 100, 100);
        cart.addItem(p, 2);
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P1", 51));
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
