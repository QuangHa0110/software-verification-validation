package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void cartAddItemInsufficientStock() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(p, 3));
        assertTrue(cart.isEmpty());
    }
}
