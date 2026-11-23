package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void cartAddItemExceedsMaxQtyThrows() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 150, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 51));
        assertTrue(cart.isEmpty());
    }
}
