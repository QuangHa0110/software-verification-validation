package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void cartTotalsUseLongMoneyAndReturnToZeroAfterLastRemoval() {
        Cart cart = new Cart();
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());

        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7_000, 150, 4);
        cart.addItem(a, 3);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_000_000L, cart.getSubtotal());
        assertEquals(600, cart.getTotalWeightGrams());
        cart.addItem(b, 4);
        assertEquals(3_000_028_000L, cart.getSubtotal());
        assertEquals(1200, cart.getTotalWeightGrams());
        assertTrue(new Cart().isEmpty());
        cart.updateQuantity("A", 0);
        cart.removeItem("B");
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(4, b.getStock());
    }
}
