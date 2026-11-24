package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void testCartAddItemExceedMaxLimitChecksBeforeStock() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 100L, 10, 3);
        cart.addItem(new Product("P1", "Pen", 100L, 10, 50), 49);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());
    }
}
