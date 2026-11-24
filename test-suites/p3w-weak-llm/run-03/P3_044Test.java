package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @Test
    void cartAddItemExceedsMaxQtyPerItem() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 100);
        cart.addItem(p, 49);

        Product pNew = new Product("P1", "Bút", 1000L, 100, 100);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(pNew, 2));

        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(49, items.get(0).getQuantity());
    }
}
