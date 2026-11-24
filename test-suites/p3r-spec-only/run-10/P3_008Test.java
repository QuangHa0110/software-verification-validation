package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void cartsAreIndependentAndComputeMoneyInLongsAndWeightInGrams() {
        Cart first = new Cart();
        List<CartItem> emptySnapshot = first.getItems();
        assertEquals(50, Cart.MAX_QTY_PER_ITEM);
        assertTrue(first.isEmpty());
        assertEquals(0L, first.getSubtotal());
        assertEquals(0, first.getTotalWeightGrams());
        Product a = new Product("A", "A", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "B", 7_000, 150, 4);
        first.addItem(a, 3);
        assertEquals(3_000_000_000L, first.getSubtotal());
        assertEquals(600, first.getTotalWeightGrams());
        first.addItem(b, 2);
        first.addItem(b, 1);
        assertFalse(first.isEmpty());
        assertTrue(emptySnapshot.isEmpty());
        assertEquals(3_000_021_000L, first.getSubtotal());
        assertEquals(1050, first.getTotalWeightGrams());
        assertEquals(3, a.getStock());
        assertEquals(4, b.getStock());
        Cart second = new Cart();
        assertTrue(second.isEmpty());
        assertTrue(second.getItems().isEmpty());
        assertEquals(2, first.getItems().size());
        first.removeItem("A");
        first.updateQuantity("B", 0);
        assertTrue(first.isEmpty());
        assertTrue(first.getItems().isEmpty());
        assertEquals(0L, first.getSubtotal());
        assertEquals(0, first.getTotalWeightGrams());
    }
}
