package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void independentCartsComputeLongSubtotalsAndReturnToEmptyAfterRemoval() {
        Cart cart = new Cart();
        Product product = new Product("P", "Book", 1_000_000_000L, 7, 3);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        cart.addItem(product, 1);
        cart.addItem(product, 2);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(21, cart.getTotalWeightGrams());
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertTrue(new Cart().isEmpty());
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, product.getStock());
    }
}
