package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void cartsAreIndependentAndTotalsReturnToZeroAfterLastRemoval() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7000, 150, 3);
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        List<CartItem> emptySnapshot = cart.getItems();
        cart.addItem(a, 3);
        assertEquals(3_000_000_000L, cart.getSubtotal());
        cart.addItem(b, 3);
        assertFalse(cart.isEmpty());
        assertTrue(emptySnapshot.isEmpty());
        assertEquals(3_000_021_000L, cart.getSubtotal());
        assertEquals(1050, cart.getTotalWeightGrams());

        Cart independent = new Cart();
        assertTrue(independent.isEmpty());
        assertEquals(2, cart.getItems().size());
        cart.removeItem("A");
        cart.updateQuantity("B", 0);
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(3, b.getStock());
    }
}
