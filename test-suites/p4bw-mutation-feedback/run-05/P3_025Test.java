package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void testCartRemoveItem() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Pen", 1000L, 10, 20);
        Product p2 = new Product("P2", "Book", 5000L, 50, 20);
        cart.addItem(p1, 2);
        cart.addItem(p2, 1);

        cart.removeItem("P1");
        assertEquals(1, cart.getItems().size());
        assertEquals("P2", cart.getItems().get(0).getProduct().getId());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("P1"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("NON_EXISTING"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("   "));
    }
}
