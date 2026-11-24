package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void cartAddItemInvalidArgumentsOrLimits() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Item", 100L, 100, 100);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, -1));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 51));

        cart.addItem(p, 49);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());
    }
}
