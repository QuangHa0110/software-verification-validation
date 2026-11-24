package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_040Test {

    @Test
    void cartAddItemAccumulationUpdatesProductReferenceAndSubtotal() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Prod 1", 10000L, 100, 50);
        cart.addItem(p1, 2);
        Product p2 = new Product("P1", "Prod 1 Updated", 12000L, 200, 50);
        cart.addItem(p2, 3);
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertSame(p2, cart.getItems().get(0).getProduct());
        assertEquals(60000L, cart.getSubtotal());
        assertEquals(1000, cart.getTotalWeightGrams());
    }
}
