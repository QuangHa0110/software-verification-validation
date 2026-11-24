package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void cartAddItemAccumulationExceedsMaxQtyThrows() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 150, 100);
        cart.addItem(p, 30);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 21));
        assertEquals(30, cart.getItems().get(0).getQuantity());
    }
}
