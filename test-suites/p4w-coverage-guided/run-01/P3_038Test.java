package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void cartAddItemExceedsStockThrows() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 150, 5);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(p, 6));
        assertTrue(cart.isEmpty());
    }
}
