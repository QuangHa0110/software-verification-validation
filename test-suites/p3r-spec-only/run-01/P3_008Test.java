package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void cartsStartIndependentAndReturnZeroTotalsAfterLastRemoval() {
        Cart cart = new Cart();
        Cart other = new Cart();
        Product p = new Product("A", "A", 1_000_000_000L, 7, 3);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        List<CartItem> emptySnapshot = cart.getItems();
        cart.addItem(p, 3);
        assertTrue(emptySnapshot.isEmpty());
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(21, cart.getTotalWeightGrams());
        cart.updateQuantity("A", 0);
        for (Cart empty : List.of(cart, other)) {
            assertTrue(empty.isEmpty());
            assertTrue(empty.getItems().isEmpty());
            assertEquals(0L, empty.getSubtotal());
            assertEquals(0, empty.getTotalWeightGrams());
        }
        assertEquals(3, p.getStock());
    }
}
