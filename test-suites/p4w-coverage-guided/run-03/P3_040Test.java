package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_040Test {

    @Test
    void emptyCartState() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertTrue(cart.getItems().isEmpty());
    }
}
