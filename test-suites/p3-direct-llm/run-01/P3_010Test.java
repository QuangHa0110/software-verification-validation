package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void replacingProductByEqualIdUsesIncomingStockPriceAndWeightAndKeepsPosition() {
        Product first = new Product(" A ", "Original", 9, 10, 1);
        Product second = new Product(" a ", "Second", 7, 5, 3);
        Product replacement = new Product(new String(" A "), "Replacement", 12, 20, 50);
        Cart cart = new Cart();
        cart.addItem(first, 1);
        cart.addItem(second, 3);
        List<CartItem> before = cart.getItems();
        cart.addItem(replacement, 49);

        List<CartItem> after = cart.getItems();
        assertEquals(2, after.size());
        assertSame(replacement, after.get(0).getProduct());
        assertEquals(50, after.get(0).getQuantity());
        assertSame(second, after.get(1).getProduct());
        assertEquals(3, after.get(1).getQuantity());
        assertEquals(621L, cart.getSubtotal());
        assertEquals(1015, cart.getTotalWeightGrams());
        assertSame(first, before.get(0).getProduct());
        assertEquals(1, before.get(0).getQuantity());
        assertEquals(1, first.getStock());
        assertEquals(3, second.getStock());
        assertEquals(50, replacement.getStock());
    }
}
