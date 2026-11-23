package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void cartRemoveItemSuccessAndNoSuchElement() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Pen", 10000L, 100, 20);
        Product p2 = new Product("P2", "Book", 20000L, 200, 10);
        cart.addItem(p1, 2);
        cart.addItem(p2, 3);

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("P3"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertEquals(2, cart.getItems().size());

        cart.removeItem("P1");
        assertEquals(1, cart.getItems().size());
        assertEquals("P2", cart.getItems().get(0).getProduct().getId());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("P1"));
    }
}
