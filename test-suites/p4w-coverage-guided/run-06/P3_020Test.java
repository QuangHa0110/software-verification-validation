package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void cartAddItemCumulativeAndReplacementWithNewInstance() {
        Cart cart = new Cart();
        Product pOld = new Product("P1", "Old Pen", 10000L, 100, 50);
        cart.addItem(pOld, 10);
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertSame(pOld, cart.getItems().get(0).getProduct());

        Product pNew = new Product("P1", "New Pen", 12000L, 110, 50);
        cart.addItem(pNew, 5);
        assertEquals(1, cart.getItems().size());
        assertEquals(15, cart.getItems().get(0).getQuantity());
        assertSame(pNew, cart.getItems().get(0).getProduct());
        assertEquals(15 * 12000L, cart.getSubtotal());
        assertEquals(15 * 110, cart.getTotalWeightGrams());
    }
}
