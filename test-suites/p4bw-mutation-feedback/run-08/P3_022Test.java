package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void cartAddItemValidation() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Name", 1000L, 10, 50);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, -1));
        assertTrue(cart.isEmpty());
    }
}
