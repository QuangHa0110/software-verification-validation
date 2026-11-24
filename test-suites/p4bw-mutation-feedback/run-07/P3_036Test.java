package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void cartAddItemCumulativeQuantityExceedsMaxThrowsException() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 60);
        cart.addItem(p1, 40);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p1, 11));
        assertEquals(40, cart.getItems().get(0).getQuantity());
    }
}
