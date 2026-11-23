package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void cartUpdateQuantityStockCheckWhenSameQuantity() {
        Cart cart = new Cart();
        Product p = new Product("A", "Món A", 1000L, 100, 5);
        cart.addItem(p, 5);

        p.reduceStock(2);
        assertEquals(3, p.getStock());

        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }
}
