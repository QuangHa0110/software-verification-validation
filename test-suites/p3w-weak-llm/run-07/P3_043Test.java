package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void cartUpdateQuantityExceedsCurrentStockThrowsInsufficientStockException() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 20);
        cart.addItem(p1, 10);
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P01", 25));
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }
}
