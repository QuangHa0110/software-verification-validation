package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void incomingProductStockControlsWhetherAnEqualIdReplacementSucceeds() {
        Cart cart = new Cart();
        Product original = new Product("P", "original", 13, 17, 50);
        Product incoming = new Product(new String("P"), "incoming", 19, 23, 2);
        cart.addItem(original, 2);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
        assertEquals(1, cart.getItems().size());
        assertSame(original, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(50, original.getStock());
        assertEquals(2, incoming.getStock());

        incoming.restoreStock(1);
        cart.addItem(incoming, 1);
        assertSame(incoming, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(57L, cart.getSubtotal());
        assertEquals(69, cart.getTotalWeightGrams());
        assertEquals(50, original.getStock());
        assertEquals(3, incoming.getStock());
    }
}
