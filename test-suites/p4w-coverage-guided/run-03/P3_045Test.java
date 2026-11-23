package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_045Test {

    @Test
    void cartAddItemExceedsMaxQtyPerItemCheckedBeforeStock() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 50);
        cart.addItem(p, 49);

        Product pNew = new Product("P1", "Bút", 1000L, 100, 3);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pNew, 2));
        assertEquals(49, cart.getItems().get(0).getQuantity());
    }
}
