package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void cartChecksMergedQuantityAgainstIncomingStockEvenWhenOriginalHasEnough() {
        Cart cart = new Cart();
        Product original = new Product("A", "Original", 10, 2, 50);
        Product incoming = new Product(new String("A"), "Incoming", 20, 3, 2);
        cart.addItem(original, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        assertEquals(1, cart.getItems().size());
        assertSame(original, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(50, original.getStock());
        assertEquals(2, incoming.getStock());
    }
}
