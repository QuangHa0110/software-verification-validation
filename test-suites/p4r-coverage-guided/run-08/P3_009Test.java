package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartsAreIndependentAndSumLineAmountsUsingLongArithmetic() {
        Cart cart = new Cart();
        List<CartItem> emptySnapshot = cart.getItems();
        Product a = new Product("A", "First", 1_000_000_000L, 200, 3);
        Product b = new Product("B", "Second", 7_000, 150, 3);
        cart.addItem(a, 3);
        assertEquals(3_000_000_000L, cart.getSubtotal());
        cart.addItem(b, 3);
        assertFalse(cart.isEmpty());
        assertEquals(3_000_021_000L, cart.getSubtotal());
        assertEquals(1_050, cart.getTotalWeightGrams());
        assertTrue(emptySnapshot.isEmpty());

        Cart other = new Cart();
        cart.removeItem("B");
        cart.updateQuantity("A", 0);
        for (Cart empty : List.of(cart, other)) {
            assertTrue(empty.isEmpty());
            assertTrue(empty.getItems().isEmpty());
            assertEquals(0L, empty.getSubtotal());
            assertEquals(0, empty.getTotalWeightGrams());
        }
        assertEquals(3, a.getStock());
        assertEquals(3, b.getStock());
    }
}
