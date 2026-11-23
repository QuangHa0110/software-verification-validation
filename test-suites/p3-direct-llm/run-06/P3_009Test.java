package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartTotalsUseAllLinesAndReturnToZeroAfterLastRemoval() {
        Cart cart = new Cart();
        Cart independent = new Cart();
        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7_000, 150, 4);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertTrue(cart.getItems().isEmpty());

        cart.addItem(a, 3);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        cart.addItem(b, 1);
        cart.addItem(b, 2);
        assertEquals(3_000_021_000L, cart.getSubtotal());
        assertEquals(1_050, cart.getTotalWeightGrams());
        assertTrue(independent.isEmpty());

        cart.removeItem("A");
        assertEquals(21_000L, cart.getSubtotal());
        cart.updateQuantity("B", 0);
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(3, a.getStock());
        assertEquals(4, b.getStock());
    }
}
