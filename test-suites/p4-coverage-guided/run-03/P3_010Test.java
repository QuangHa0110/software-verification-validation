package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void updateReplacesQuantityAtSamePositionAndZeroRemovesEvenWhenStockIsGone() {
        Product a = new Product("A", "A", 10, 4, 50);
        Product b = new Product("B", "B", 3, 7, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        cart.updateQuantity("A", 50);
        assertEquals(503L, cart.getSubtotal());
        assertEquals(207, cart.getTotalWeightGrams());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertEquals(50, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("A", 1);
        assertEquals(13L, cart.getSubtotal());
        assertEquals(11, cart.getTotalWeightGrams());
        a.reduceStock(50);
        cart.updateQuantity("A", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        cart.removeItem("B");
        assertTrue(cart.isEmpty());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(0, a.getStock());
        assertEquals(1, b.getStock());
    }
}
