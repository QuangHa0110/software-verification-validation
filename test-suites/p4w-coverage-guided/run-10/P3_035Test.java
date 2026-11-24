package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void testCartRemoveItemSuccessAndNoSuchElement() {
        Cart cart = new Cart();
        Product p1 = new Product("A", "A", 10L, 10, 5);
        Product p2 = new Product("B", "B", 20L, 10, 5);
        cart.addItem(p1, 2);
        cart.addItem(p2, 3);

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("A"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("NON_EXISTENT"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
    }
}
