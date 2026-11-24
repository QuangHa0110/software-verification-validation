package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void testCartAddItemAccumulateQuantityAndReplaceProductInstance() {
        Cart cart = new Cart();
        Product pA1 = new Product("A", "Prod A Old", 9L, 10, 1);
        Product pB = new Product("B", "Prod B", 5L, 20, 5);
        cart.addItem(pA1, 1);
        cart.addItem(pB, 1);

        Product pA2 = new Product("A", "Prod A New", 12L, 15, 50);
        cart.addItem(pA2, 49);

        assertEquals(2, cart.getItems().size());
        assertEquals("A", cart.getItems().get(0).getProduct().getId());
        assertEquals(50, cart.getItems().get(0).getQuantity());
        assertSame(pA2, cart.getItems().get(0).getProduct());
        assertEquals(12L * 50, cart.getItems().get(0).getLineTotal());

        assertEquals("B", cart.getItems().get(1).getProduct().getId());
    }
}
