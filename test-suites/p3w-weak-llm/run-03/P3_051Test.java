package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_051Test {

    @Test
    void cartUpdateQuantityInsufficientStock() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 3);
        cart.addItem(p, 2);

        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P1", 4));
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
