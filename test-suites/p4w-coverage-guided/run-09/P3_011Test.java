package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartEmptyAndBasicOperations() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertTrue(cart.getItems().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> cart.getItems().add(new CartItem(new Product("P", "N", 10L, 10, 10), 1)));
    }
}
