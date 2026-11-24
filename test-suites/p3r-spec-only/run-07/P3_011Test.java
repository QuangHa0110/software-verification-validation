package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void independentCartsReportEmptyTotalsBeforeAdditionAndAfterLastRemoval() {
        Cart cart = new Cart();
        Product p = new Product("P", "N", 1_000_000_000L, 17, 3);
        List<CartItem> emptySnapshot = cart.getItems();
        cart.addItem(p, 3);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(51, cart.getTotalWeightGrams());
        assertTrue(emptySnapshot.isEmpty());

        Cart independent = new Cart();
        assertTrue(independent.isEmpty());
        assertTrue(independent.getItems().isEmpty());
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, p.getStock());
    }
}
