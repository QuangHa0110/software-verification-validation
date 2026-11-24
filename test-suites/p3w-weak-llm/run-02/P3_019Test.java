package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void cartRemoveItemAndReAddOrder() {
        Cart cart = new Cart();
        Product pA = new Product("A", "Món A", 1000L, 100, 10);
        Product pB = new Product("B", "Món B", 2000L, 100, 10);

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("A"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(""));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("   "));

        cart.addItem(pA, 2);
        cart.addItem(pB, 3);

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("C"));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());

        cart.addItem(pA, 1);
        assertEquals(2, cart.getItems().size());
        assertEquals("B", cart.getItems().get(0).getProduct().getId());
        assertEquals("A", cart.getItems().get(1).getProduct().getId());
    }
}
