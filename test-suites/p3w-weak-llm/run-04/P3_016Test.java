package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void cartAddItemAccumulateAndReplaceProductReference() {
        Cart cart = new Cart();
        Product p1Old = new Product("P1", "Old Name", 9L, 100, 1);
        Product p2 = new Product("P2", "Second", 50L, 200, 10);
        Product p1New = new Product("P1", "New Name", 12L, 150, 50);

        cart.addItem(p1Old, 1);
        cart.addItem(p2, 1);
        cart.addItem(p1New, 49);

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertEquals("P1", items.get(0).getProduct().getId());
        assertEquals(50, items.get(0).getQuantity());
        assertSame(p1New, items.get(0).getProduct());
        assertEquals(600L, items.get(0).getLineTotal());
        assertEquals("P2", items.get(1).getProduct().getId());
    }
}
