package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void cartAddItemInvalidQuantityThrows(int qty) {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 150, 10);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, qty));
        assertTrue(cart.isEmpty());
    }
}
