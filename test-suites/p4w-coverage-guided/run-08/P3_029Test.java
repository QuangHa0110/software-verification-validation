package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void cartRemoveItemSuccessAndNoSuchElement() {
        Cart cart = new Cart();
        Product pA = new Product("A", "Product A", 100L, 10, 10);
        Product pB = new Product("B", "Product B", 200L, 20, 10);
        cart.addItem(pA, 2);
        cart.addItem(pB, 3);

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("NON_EXISTENT"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(""));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());

        cart.removeItem("B");
        assertTrue(cart.isEmpty());
    }
}
