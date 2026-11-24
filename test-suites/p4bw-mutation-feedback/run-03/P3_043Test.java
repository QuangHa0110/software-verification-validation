package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void cartAddItemAccumulateSameProductMaintainsPositionAndUpdatesProductRef() {
        Cart cart = new Cart();
        Product p1Old = new Product("P1", "Bút cũ", 9L, 10, 1);
        Product p2 = new Product("P2", "Vở", 20L, 10, 10);
        Product p1New = new Product("P1", "Bút mới", 12L, 10, 50);

        cart.addItem(p1Old, 1);
        cart.addItem(p2, 1);
        cart.addItem(p1New, 49);

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertEquals("P1", items.get(0).getProduct().getId());
        assertEquals("Bút mới", items.get(0).getProduct().getName());
        assertEquals(50, items.get(0).getQuantity());
        assertEquals(12L * 50, items.get(0).getLineTotal());
        assertEquals("P2", items.get(1).getProduct().getId());
    }
}
