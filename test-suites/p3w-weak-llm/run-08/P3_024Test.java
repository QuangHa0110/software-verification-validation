package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void cartAddItemCumulativeAndProductReplacement() {
        Cart cart = new Cart();
        Product pA1 = new Product("A", "Old A", 9L, 10, 1);
        cart.addItem(pA1, 1);
        Product pB = new Product("B", "B Item", 100L, 20, 10);
        cart.addItem(pB, 1);

        Product pA2 = new Product("A", "New A", 12L, 10, 50);
        cart.addItem(pA2, 49);

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertEquals("A", items.get(0).getProduct().getId());
        assertEquals(50, items.get(0).getQuantity());
        assertSame(pA2, items.get(0).getProduct());
        assertEquals(600L, items.get(0).getLineTotal());

        assertEquals("B", items.get(1).getProduct().getId());
        assertEquals(1, items.get(1).getQuantity());
    }
}
