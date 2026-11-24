package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void cartRemoveItemValidAndInvalid() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Item 1", 100L, 100, 10);
        Product p2 = new Product("P2", "Item 2", 200L, 200, 10);
        cart.addItem(p1, 2);
        cart.addItem(p2, 3);

        cart.removeItem("P1");
        assertEquals(1, cart.getItems().size());
        assertEquals("P2", cart.getItems().get(0).getProduct().getId());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("P1"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(""));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("   "));
    }
}
