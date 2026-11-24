package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void cartAddItemUpdatesProductReferenceAndQuantity() {
        Cart cart = new Cart();
        Product pOld = new Product("P01", "Old Book", 100000L, 200, 30);
        cart.addItem(pOld, 10);
        Product pNew = new Product("P01", "New Book", 120000L, 250, 30);
        cart.addItem(pNew, 5);
        assertEquals(1, cart.getItems().size());
        assertSame(pNew, cart.getItems().get(0).getProduct());
        assertEquals(15, cart.getItems().get(0).getQuantity());
        assertEquals(1800000L, cart.getSubtotal());
        assertEquals(3750, cart.getTotalWeightGrams());
    }
}
